package com.strategists.game.service.impl;

import com.strategists.game.configuration.properties.ExternalAPIEndpointConfigurationProperties;
import com.strategists.game.configuration.properties.PredictionsConfigurationProperties;
import com.strategists.game.csv.impl.PredictionsCSV;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.GameMap;
import com.strategists.game.entity.PlayerPrediction;
import com.strategists.game.repository.PlayerPredictionRepository;
import com.strategists.game.request.DownloadGoogleDriveFilesRequest;
import com.strategists.game.response.DownloadGoogleDriveFilesResponse;
import com.strategists.game.response.PlayerPredictionsResponse;
import com.strategists.game.response.PredictionsModelInfo;
import com.strategists.game.service.HistoryService;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;
import com.strategists.game.service.PredictionsService;
import com.strategists.game.service.StorageService;
import com.strategists.game.update.UpdateMapping;
import com.strategists.game.update.UpdateType;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
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

    @Autowired
    private HistoryService historyService;

    @PostConstruct
    public void setup() {
        log.info("Predictions enabled! Strategies: {}", properties.strategies());

        // Handle legacy predictions data directory creation
        handleLegacyDataDirectoryCreation();

        // Handle legacy predictions data download
        handleLegacyDataDownload();

        // Training predictions model on start-up
        trainEligiblePredictionsModels();
    }

    @Override
    @Transactional
    public void trainPredictionsModel(Game game) {
        // Exporting legacy predictions CSV file if enabled
        exportLegacyPredictionsCSV(game);

        // Checking if training on end enabled
        if (!properties.strategies().trainOnEndEnabled()) {
            log.warn("Model training on end skipped for game: {}", game.getCode());
            return;
        }
        log.info("Training predictions model for game: {}", game.getCode());

        trainPredictionsModel(game.getGameMapId());
    }

    @Override
    @Transactional
    @UpdateMapping(UpdateType.PREDICTION)
    public List<PlayerPrediction> inferPredictionsModel(Game game) {
        // Checking if model inference is disabled
        if (!properties.strategies().modelInferenceEnabled()) {
            log.warn("Model inference skipped for game: {}", game.getCode());
            return List.of();
        }
        log.info("Inferring predictions model for game: {}", game.getCode());

        // Checking if predictions model exists for the game map
        if (!doesPredictionsModelExists(game.getGameMapId())) {
            log.warn("No predictions model info found, skipping inference for game: {}", game.getCode());
            return List.of();
        }

        // Checking if we have history for the game, or if its valid
        final var history = historyService.getHistory(game);
        if (CollectionUtils.isEmpty(history)) {
            log.warn("No history found for game, skipping inference for game: {}", game.getCode());
            return List.of();
        }

        // Invoking inference API
        final var playerPredictionsResponseOptional = invokeInferModelAPIEndpoint(game.getGameMapId(), history);
        if (playerPredictionsResponseOptional.isEmpty()) {
            log.warn("No response found from inference API for game: {}", game.getCode());
            return List.of();
        }

        // Persisting player predictions
        final var playerPredictions = playerPredictionsResponseOptional.get().getPlayerPredictions().stream().map(response -> {
            final var player = playerService.getPlayerById(response.getPlayerId());
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
        // Training model on start-up if enabled
        if (!properties.strategies().trainOnStartupEnabled()) {
            log.warn("Predictions model training on start-up skipped!");
            return;
        }

        log.info("Training predictions model for eligible game maps...");
        for (final var file : gameMapFiles.values()) {
            final var gameMap = GameMap.from(file);
            if (Objects.isNull(gameMap)) {
                continue;
            }

            // Checking if predictions model exists for the game map
            if (doesPredictionsModelExists(gameMap.getId())) {
                log.warn("Predictions model info found, skipping model training for map: '{}'", gameMap.getId());
                continue;
            }

            // Sending train request for the model
            trainPredictionsModel(gameMap.getId());
        }
    }

    private void trainPredictionsModel(String gameMapId) {
        log.info("Training predictions model for game map ID: '{}'", gameMapId);
        final var opt = invokeTrainModelAPIEndpoint(gameMapId);
        if (opt.isPresent()) {
            final var response = opt.get();
            log.info("Predictions model trained for game map ID: '{}' | Response: {}", gameMapId, response);
        } else {
            log.warn("No predictions model trained for game map ID: '{}'", gameMapId);
        }
    }

    private boolean doesPredictionsModelExists(String gameMapId) {
        return invokeGetModelAPIEndpoint(gameMapId).isPresent();
    }

    private Optional<PredictionsModelInfo> invokeGetModelAPIEndpoint(String gameMapId) {
        // Checking if API call is by-passed!
        if (properties.getModel().bypassForTesting()) {
            log.warn("Bypassing get model info for testing! This should only happen for local testing!");
            return Optional.empty();
        }

        // Getting model info
        try {
            final var endpoint = properties.getModel().apiEndpoint().replace("{game_map_id}", gameMapId);
            final var restTemplate = new RestTemplate();
            final var response = restTemplate.getForObject(endpoint, PredictionsModelInfo.class);
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
            final var endpoint = properties.trainModel().apiEndpoint().replace("{game_map_id}", gameMapId);
            final var restTemplate = new RestTemplate();
            final var response = restTemplate.postForObject(endpoint, null, PredictionsModelInfo.class);
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

        // Inferring model
        try {
            final var endpoint = properties.inferModel().apiEndpoint().replace("{game_map_id}", gameMapId);
            final var body = Map.of("data", data);
            final var restTemplate = new RestTemplate();
            final var response = restTemplate.postForObject(endpoint, body, PlayerPredictionsResponse.class);
            return Optional.ofNullable(response);
        } catch (Exception ex) {
            log.error("Unable to infer the model for game map ID '{}'! Reason {}", gameMapId, ex.getMessage());
            return Optional.empty();
        }
    }

    private void handleLegacyDataDirectoryCreation() {
        // Checking if legacy data export or download enabled
        if (!properties.legacy().enabled()) {
            return;
        }
        log.warn("Legacy predictions features enabled! Ensuring data directory is created...");

        // Checking if legacy predictions data directory exists
        final var dataDirectory = properties.legacy().dataDirectory();
        if (!dataDirectory.exists()) {
            Assert.state(dataDirectory.mkdirs(), "Unable to create directory: " + dataDirectory);
            log.info("Created directory: {}", dataDirectory);
        }
    }

    private void handleLegacyDataDownload() {
        // Checking if legacy data download is enabled
        if (!properties.legacy().googleDrive().enabled()) {
            return;
        }
        log.warn("Legacy predictions data download enabled! Downloading predictions legacy data...");

        // Downloading legacy predictions CSV files
        final var request = new DownloadGoogleDriveFilesRequest();
        request.setGoogleDriveFolderId(properties.legacy().googleDrive().folderId());
        request.setMimetype("text/csv");
        request.setLocalDataDirectory(properties.legacy().dataDirectory());
        final var response = storageService.downloadGoogleDriveFiles(request);

        // Logging how many new files downloaded
        final var downloadedFiles = response
                .map(DownloadGoogleDriveFilesResponse::getDownloadedFiles)
                .orElse(List.of());
        log.info("New predictions legacy CSV files downloaded: {}", downloadedFiles.size());
    }

    private void exportLegacyPredictionsCSV(Game game) {
        // Checking if legacy data export enabled
        if (!properties.legacy().export().enabled()) {
            return;
        }
        log.warn("Exporting legacy predictions CSV file for game: {}", game.getCode());

        // Loading players based on bankruptcy order
        final var orderedPlayers = playerService.getPlayersByGameOrderByBankruptcy(game);

        // Exporting legacy predictions CSV
        final var predictionsCSV = new PredictionsCSV(game, landService.getLandsByGame(game), orderedPlayers);
        try {
            final var predictionsCSVFile = predictionsCSV.export(properties.legacy().dataDirectory());
            log.info("Exported legacy predictions CSV at path: {}", predictionsCSVFile.getAbsolutePath());
        } catch (IOException ex) {
            log.error("Unable to export legacy predictions CSV file! Message: {}", ex.getMessage());
            log.debug(ex);
        }
    }

    @Override
    protected ExternalAPIEndpointConfigurationProperties getHealthCheck() {
        return properties.healthCheck();
    }

    @Override
    protected String getExternalServiceName() {
        return "strategists-predictions";
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
