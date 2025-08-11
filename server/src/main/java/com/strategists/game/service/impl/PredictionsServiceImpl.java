package com.strategists.game.service.impl;

import com.strategists.game.configuration.properties.PredictionsConfigurationProperties;
import com.strategists.game.csv.impl.PredictionsCSV;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.GameMap;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerPrediction;
import com.strategists.game.repository.PlayerPredictionRepository;
import com.strategists.game.request.DownloadGoogleDriveFilesRequest;
import com.strategists.game.request.UploadLocalFilesRequest;
import com.strategists.game.response.DownloadGoogleDriveFilesResponse;
import com.strategists.game.response.PlayerPredictionsResponse;
import com.strategists.game.response.PredictionsModelInfo;
import com.strategists.game.response.UploadLocalFilesResponse;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;
import com.strategists.game.service.PredictionsService;
import com.strategists.game.service.StorageService;
import com.strategists.game.update.UpdateMapping;
import com.strategists.game.update.UpdateType;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.ConnectException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@Service
@ConditionalOnProperty(name = "strategists.predictions.enabled", havingValue = "true")
public class PredictionsServiceImpl extends AbstractExternalService implements PredictionsService {

    @Autowired
    private PredictionsConfigurationProperties properties;

    @Autowired
    private PlayerPredictionRepository playerPredictionRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private Map<String, File> gameMapFiles;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private LandService landService;

    public PredictionsServiceImpl() {
        super("strategists-predictions", log);
    }

    @PostConstruct
    public void setup() throws InterruptedException, ConnectException {
        waitUntilReady(properties.healthCheck());
        log.info("Predictions enabled! Strategies: {}", properties.strategies());

        // Checking if predictions CSV data directory exists
        var exportDirectory = properties.dataDirectory();
        if (!exportDirectory.exists()) {
            Assert.state(exportDirectory.mkdirs(), "Unable to create directory: " + exportDirectory);
            log.info("Created directory: {}", exportDirectory);
        }

        // Training predictions model on start-up
        trainEligiblePredictionsModels();
    }

    @Override
    @Transactional
    public void trainPredictionsModel(Game game) {
        log.info("Training predictions model for game: {}", game.getCode());

        // Loading players based on bankruptcy order
        val orderedPlayers = playerService.getPlayersByGameOrderByBankruptcy(game);

        // Checking if game data CSV should be exported
        Exception validationException = null;
        try {
            validateGameIntegrity(game, orderedPlayers);
        } catch (Exception ex) {
            if (properties.strategies().dataIntegrityValidationEnabled()) {
                log.warn("Skipped CSV export for game: {} | Reason: {}", game.getCode(), ex.getMessage());
                return;
            } else {
                log.warn("CSV validation by-passed | Reason: {}", ex.getMessage());
                validationException = ex;
            }
        }

        // Exporting Prediction CSV
        var predictionsCSV = new PredictionsCSV(game, landService.getLandsByGame(game), orderedPlayers);
        File predictionsCSVFile;
        try {
            // Checking if data export is enabled
            if (!properties.strategies().dataExportEnabled()) {
                throw new RuntimeException("Predictions CSV export disabled!");
            }

            // Exporting predictions CSV file with appropriate file name
            if (Objects.nonNull(validationException)) {
                var fileName = String.format("%s-[ISSUE: %s]", predictionsCSV.getDefaultFileName(), validationException.getMessage());
                predictionsCSVFile = predictionsCSV.export(properties.dataDirectory(), fileName);
            } else {
                predictionsCSVFile = predictionsCSV.export(properties.dataDirectory());
            }
        } catch (Exception ex) {
            log.warn("Skipping training the model because no CSV exported! Reason: {}", ex.getMessage());
            return;
        }

        // Uploading exported predictions CSV file
        log.info("Exported predictions CSV at path: {}", predictionsCSVFile.getAbsolutePath());
        if (CollectionUtils.isEmpty(uploadPredictionsCSV().map(UploadLocalFilesResponse::getUploadedFiles).orElse(List.of()))) {
            log.warn("No new file uploaded to Google Drive, skipping training model!");
            return;
        }

        // Invoking training API if training on end enabled
        if (!properties.strategies().trainOnEndEnabled()) {
            log.warn("Model training on end by-passed for game: {}", game.getCode());
            return;
        }
        invokeTrainModelAPIEndpoint(game.getGameMapId()).ifPresentOrElse(
                response -> log.info("Predictions model trained for game: {} | Response: {}", game.getCode(), response),
                () -> log.warn("No predictions model trained for game: {}", game.getCode())
        );
    }

    @Override
    @Transactional
    @UpdateMapping(UpdateType.PREDICTION)
    public List<PlayerPrediction> inferPredictionsModel(Game game) {
        log.info("Inferring predictions model for game: {}", game.getCode());

        // Checking if model inference is disabled
        if (!properties.strategies().modelInferenceEnabled()) {
            log.warn("Model inference by-passed!");
            return List.of();
        }

        // Checking if predictions model exists for the game map
        var predictionsModelInfoOptional = invokeGetModelAPIEndpoint(game.getGameMapId());
        if (predictionsModelInfoOptional.isEmpty()) {
            log.warn("No predictions model info found, skipping inference!");
            return List.of();
        }

        // Invoking inference API
        var predictionsCSV = new PredictionsCSV(game, landService.getLandsByGame(game), playerService.getActivePlayersByGame(game));
        var playerPredictionsResponseOptional = invokeInferModelAPIEndpoint(game.getGameMapId(), predictionsCSV.getRowMaps());
        if (playerPredictionsResponseOptional.isEmpty()) {
            log.warn("No response found from inference API!");
            return List.of();
        }

        // Persisting player predictions
        var playerPredictions = playerPredictionsResponseOptional.get().getPlayerPredictions().stream().map(response -> {
            var player = playerService.getPlayerById(response.getPlayerId());
            return new PlayerPrediction(player, response.getBankruptProbability(), response.getWinnerProbability(), response.getPrediction());
        }).toList();
        return playerPredictionRepository.saveAll(playerPredictions);
    }

    @Override
    public List<PlayerPrediction> getPlayerPredictionsByGame(Game game) {
        return playerPredictionRepository.findByGameOrderById(game);
    }

    @Override
    public void clearPlayerPredictions(Game game) {
        playerPredictionRepository.deleteByGame(game);
    }

    private void trainEligiblePredictionsModels() {
        // Downloading predictions CSV
        var downloadedFiles = downloadPredictionsCSV().map(DownloadGoogleDriveFilesResponse::getDownloadedFiles).orElse(List.of());
        log.info("New predictions CSV files downloaded: {}", downloadedFiles.size());

        // Training model on start-up if enabled
        if (!properties.strategies().trainOnStartupEnabled()) {
            log.warn("Predictions model training on start-up by-passed!");
            return;
        }

        log.info("Training predictions model for eligible game maps...");
        for (var file : gameMapFiles.values()) {
            var gameMap = GameMap.from(file);
            if (Objects.isNull(gameMap)) {
                continue;
            }

            // Checking whether new model training required for this map
            // Case 1 - new files downloaded for the given game map
            // Case 2 - no model trained for the given game map
            var hasDownloadedFiles = downloadedFiles.stream().anyMatch(name -> name.startsWith(gameMap.getId()));
            if (hasDownloadedFiles || invokeGetModelAPIEndpoint(gameMap.getId()).isEmpty()) {
                log.info("Training predictions model for game map ID: '{}'", gameMap.getId());
                invokeTrainModelAPIEndpoint(gameMap.getId()).ifPresentOrElse(
                        response -> log.info("Predictions model trained for game map ID: '{}' | Response: {}", gameMap.getId(), response),
                        () -> log.warn("No predictions model trained for game map ID: '{}'", gameMap.getId())
                );
            } else {
                log.info("Skipping training predictions model for game map ID: '{}'", gameMap.getId());
            }
        }
    }

    private Optional<PredictionsModelInfo> invokeGetModelAPIEndpoint(String gameMapId) {
        // Checking if API call is by-passed!
        if (properties.getModel().bypassForTesting()) {
            log.warn("Bypassing get model info for testing! This should only happen for local testing!");
            return Optional.empty();
        }

        try {
            var endpoint = properties.getModel().apiEndpoint().replace("{game_map_id}", gameMapId);
            var restTemplate = new RestTemplate();
            var response = restTemplate.getForObject(endpoint, PredictionsModelInfo.class);
            return Optional.ofNullable(response);
        } catch (Exception ex) {
            log.error("Unable to get predictions model info for game map ID: '{}' | Reason: {}", gameMapId, ex.getMessage());
            return Optional.empty();
        }
    }

    private Optional<PredictionsModelInfo> invokeTrainModelAPIEndpoint(String gameMapId) {
        // Checking if API call is by-passed!
        if (properties.trainModel().bypassForTesting()) {
            log.warn("Bypassing model training for testing! This should only happen for local testing!");
            return Optional.empty();
        }

        // Training model
        try {
            var endpoint = properties.trainModel().apiEndpoint().replace("{game_map_id}", gameMapId);
            var restTemplate = new RestTemplate();
            var response = restTemplate.postForObject(endpoint, null, PredictionsModelInfo.class);
            return Optional.ofNullable(response);
        } catch (Exception ex) {
            log.error("Unable to train the model for game map ID '{}'! Reason: {}", gameMapId, ex.getMessage());
            return Optional.empty();
        }
    }

    private Optional<PlayerPredictionsResponse> invokeInferModelAPIEndpoint(String gameMapId, List<Map<String, Object>> data) {
        // Checking if API call is by-passed!
        if (properties.inferModel().bypassForTesting()) {
            log.warn("Bypassing model inferring for testing! This should only happen for local testing!");
            return Optional.empty();
        }

        // Training model
        try {
            var endpoint = properties.inferModel().apiEndpoint().replace("{game_map_id}", gameMapId);
            var body = Map.of("data", data);
            var restTemplate = new RestTemplate();
            var response = restTemplate.postForObject(endpoint, body, PlayerPredictionsResponse.class);
            return Optional.ofNullable(response);
        } catch (Exception ex) {
            log.error("Unable to infer the model for game map ID '{}'! Reason {}", gameMapId, ex.getMessage());
            return Optional.empty();
        }
    }

    private void validateGameIntegrity(Game game, List<Player> players) {
        val case1 = players.size() > 1;
        Assert.isTrue(case1, "More than 1 player required!");

        val case2 = Objects.isNull(game.getAllowedSkipsCount());
        if (!case2) {
            val case3 = players.stream().allMatch(player -> player.getRemainingSkipsCount() > 0);
            Assert.isTrue(case3, "All players must have more than 0 remaining skips!");
        }

        val activePlayers = players.stream().filter(player -> !player.isBankrupt()).toList();
        val bankruptPlayers = players.stream().filter(Player::isBankrupt).toList();

        val case4 = activePlayers.size() == 1;
        val case5 = bankruptPlayers.size() == players.size() - 1;
        val case6 = activePlayers.size() + bankruptPlayers.size() == players.size();
        Assert.isTrue(case4, "Only 1 active player should remain!");
        Assert.isTrue(case5, "Apart from 1 active player, all other players should be bankrupt!");
        Assert.isTrue(case6, "Active & bankrupt players count should add up to total players count!");

        int order = 1;
        for (Player player : players) {
            Assert.isTrue(player.getBankruptcyOrder() == order++, "Inconsistent bankruptcy order!");
        }
    }

    private Optional<DownloadGoogleDriveFilesResponse> downloadPredictionsCSV() {
        var request = new DownloadGoogleDriveFilesRequest();
        request.setGoogleDriveFolderId(properties.googleDrive().downloadFolderId());
        request.setMimetype("text/csv");
        request.setLocalDataDirectory(properties.dataDirectory());
        return storageService.downloadGoogleDriveFiles(request);
    }

    private Optional<UploadLocalFilesResponse> uploadPredictionsCSV() {
        var request = new UploadLocalFilesRequest();
        request.setGoogleDriveFolderId(properties.googleDrive().uploadFolderId());
        request.setReferenceGoogleDriveFolderId(properties.googleDrive().downloadFolderId());
        request.setMimetype("text/csv");
        request.setLocalDataDirectory(properties.dataDirectory());
        return storageService.uploadLocalFiles(request);
    }
}
