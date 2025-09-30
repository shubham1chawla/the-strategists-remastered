package com.strategists.game.service.impl;

import com.strategists.game.advice.AdviceContext;
import com.strategists.game.advice.handler.AbstractAdviceHandler;
import com.strategists.game.csv.impl.AdvicesCSV;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.repository.AdviceRepository;
import com.strategists.game.request.UploadLocalFilesRequest;
import com.strategists.game.service.AdvicesService;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;
import com.strategists.game.service.StorageService;
import com.strategists.game.update.UpdateMapping;
import com.strategists.game.update.UpdateType;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.chain.impl.ChainBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Log4j2
@Service
@Transactional
@ConditionalOnProperty(name = "strategists.advices.enabled", havingValue = "true")
public class AdvicesServiceImpl implements AdvicesService {

    @Value("${strategists.advices.data-directory}")
    private File dataDirectory;

    @Value("${strategists.advices.google-drive.upload-folder-id}")
    private String uploadGoogleDriveFolderId;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private LandService landService;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private AdviceRepository adviceRepository;

    @Autowired
    private List<AbstractAdviceHandler> handlers;

    @PostConstruct
    public void setup() {
        log.info("Advices enabled! Total handlers registered: {}", handlers.size());

        // Validating export directory
        if (!dataDirectory.exists()) {
            Assert.state(dataDirectory.mkdir(), "Unable to create directory: " + dataDirectory);
            log.info("Created directory: {}", dataDirectory);
        }
    }

    @Override
    @UpdateMapping(UpdateType.ADVICE)
    public List<Advice> generateAdvices(Game game) {
        log.info("Generating advices for game: {}", game.getCode());

        // Checking if any handler is available
        if (CollectionUtils.isEmpty(handlers)) {
            log.warn("No advice handlers enabled. Skipping generating advices...");
            return List.of();
        }

        // Creating the chain from available handlers
        final var chain = new ChainBase();
        for (AbstractAdviceHandler handler : handlers) {
            chain.addCommand(handler);
        }

        // Adding information to advice context
        final var players = playerService.getPlayersByGame(game);
        final var lands = landService.getLandsByGame(game);
        final var activities = activityRepository.findByGameOrderByIdDesc(game);
        final var context = new AdviceContext(game, players, lands, activities);

        // Executing advice chain
        try {
            chain.execute(context);
        } catch (Exception ex) {
            log.error("Unable to complete advice chain! Message: {}", ex.getMessage(), ex);
            return List.of();
        }

        // Saving new and updated records
        return adviceRepository.saveAll(context.getAdvices());
    }

    @Override
    public List<Advice> getAdvicesByGame(Game game) {
        return adviceRepository.findByGameOrderByPriority(game);
    }

    @Override
    @UpdateMapping(UpdateType.ADVICE)
    public List<Advice> markPlayerAdvicesViewed(Player player) {
        log.info("Marking {}'s advices as viewed for game: {}", player.getUsername(), player.getGameCode());
        final var advices = adviceRepository.findByPlayerAndViewed(player, false);
        if (CollectionUtils.isEmpty(advices)) {
            return List.of();
        }
        return adviceRepository.saveAll(advices.stream().peek(advice -> advice.setViewed(true)).toList());
    }

    @Override
    public void clearAdvices(Game game) {
        adviceRepository.deleteByGame(game);
    }

    @Override
    public void exportAdvices(Game game) {
        // Preparing advices CSV
        final var advices = getAdvicesByGame(game);
        final var advicesCSV = new AdvicesCSV(game, advices);

        // Exporting advices CSV file
        File advicesCSVFile;
        try {
            advicesCSVFile = advicesCSV.export(dataDirectory);
        } catch (IOException ex) {
            log.warn("Unable to export Advice's CSV file! Message: {}", ex.getMessage());
            return;
        }

        // Uploading exported advices CSV file
        log.info("Exported advices CSV at path: {}", advicesCSVFile.getAbsolutePath());
        uploadAdvicesCSV();
    }

    private void uploadAdvicesCSV() {
        final var request = new UploadLocalFilesRequest();
        request.setGoogleDriveFolderId(uploadGoogleDriveFolderId);
        request.setMimetype("text/csv");
        request.setLocalDataDirectory(dataDirectory);
        storageService.uploadLocalFiles(request);
    }

}
