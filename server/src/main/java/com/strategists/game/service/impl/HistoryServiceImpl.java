package com.strategists.game.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strategists.game.configuration.properties.HistoryConfigurationProperties;
import com.strategists.game.entity.Game;
import com.strategists.game.request.DownloadGoogleDriveFilesRequest;
import com.strategists.game.request.UploadLocalFilesRequest;
import com.strategists.game.response.DownloadGoogleDriveFilesResponse;
import com.strategists.game.response.UploadLocalFilesResponse;
import com.strategists.game.service.HistoryService;
import com.strategists.game.service.StorageService;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.UpdatePayload;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Log4j2
@Service
public class HistoryServiceImpl implements HistoryService {

    private static final String FILE_EXTENSION = ".jsonl";

    @Autowired
    private HistoryConfigurationProperties properties;

    @Autowired(required = false)
    private StorageService storageService;

    // Game Code -> Pair(JSON strings, Whether valid or not)
    private final Map<String, Pair<Queue<String>, Boolean>> history = new ConcurrentHashMap<>();

    @PostConstruct
    public void setup() {
        log.info(properties);

        // Checking if history data directory exists
        final var dataDirectory = properties.dataDirectory();
        if (!dataDirectory.exists()) {
            Assert.state(dataDirectory.mkdirs(), "Unable to create directory: " + dataDirectory);
            log.info("Created directory: {}", dataDirectory);
        }

        // Downloading history files from Google Drive
        downloadHistoryFiles();
    }

    @Override
    public List<Map<String, Object>> getHistory(Game game) {
        // Checking if we have history for the game
        if (!history.containsKey(game.getCode())) {
            log.warn("No history found for game: {}", game.getCode());
            return List.of();
        }

        // Checking if history is valid
        final var pair = history.get(game.getCode());
        if (!pair.getSecond()) {
            log.warn("History not valid for game: {}", game.getCode());
            return List.of();
        }

        // Deserializing history JSONs
        final var mapper = new ObjectMapper();
        final var entries = new ArrayList<Map<String, Object>>();
        for (final var json : pair.getFirst()) {
            try {
                final var entry = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
                });
                entries.add(entry);
            } catch (Exception ex) {
                log.error("Unable to deserialize JSON: {}", json, ex);
                return List.of();
            }
        }
        return entries;
    }

    @Override
    public void appendUpdatePayload(Game game, UpdatePayload<?> payload) {
        // Checking if PING payload, ignore (this will never be called with ping as there is no handler)
        if (UpdateType.PING.equals(payload.getType())) {
            return;
        }

        // Checking if CLEAN_UP payload, reset history (sent via SchedulerService after delay and has handler)
        if (UpdateType.CLEAN_UP.equals(payload.getType())) {
            resetHistory(game);
            return;
        }

        // Checking if game's history is present or if game resets (RESET update takes first place similar to CREATE)
        if (!history.containsKey(game.getCode()) || UpdateType.RESET.equals(payload.getType())) {
            history.put(game.getCode(), Pair.of(new ConcurrentLinkedQueue<>(), true));
        }

        // Appending payload to history
        try {
            final var json = getUpdatePayloadJson(payload);
            history.get(game.getCode()).getFirst().add(json);
        } catch (JsonProcessingException ex) {
            log.error("Unable to serialize payload: {}", payload, ex);

            // Invalidating history to prevent export
            history.computeIfPresent(game.getCode(), (k, pair) -> Pair.of(pair.getFirst(), false));
        }
    }

    @Override
    public void exportHistory(Game game) {
        // Checking if game's history is present
        if (!history.containsKey(game.getCode()) || CollectionUtils.isEmpty(history.get(game.getCode()).getFirst())) {
            log.warn("No history available for game: {}", game.getCode());
            if (CollectionUtils.isEmpty(history.get(game.getCode()).getFirst())) {
                resetHistory(game);
            }
            return;
        }

        // Checking if history is valid
        final var pair = history.get(game.getCode());
        if (!pair.getSecond()) {
            log.warn("History is not valid for game: {}", game.getCode());
            resetHistory(game);
            return;
        }

        // Joining JSON strings into one multi-line JSON blob
        final var jsonl = String.join("\n", pair.getFirst()) + "\n";

        // Preparing multi-line JSON file for writing the payload (map-code-timestamp.jsonl)
        final var filename = String.format("%s-%s-%s%s", game.getGameMapId(), game.getCode(), System.currentTimeMillis(), FILE_EXTENSION);
        final var file = new File(properties.dataDirectory(), filename);

        // Appending payload's JSON string to the multi-line JSON file
        try (final var writer = new FileWriter(file, true)) {
            writer.write(jsonl);
            log.info("Exported history at path: {}", file.getAbsolutePath());
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }

        // Removing game's history from map
        resetHistory(game);

        // Uploading to Google Drive
        uploadHistoryFiles();
    }

    @Override
    public void resetHistory(Game game) {
        log.info("Resetting history for game: {}", game.getCode());
        history.remove(game.getCode());
    }


    private String getUpdatePayloadJson(UpdatePayload<?> payload) throws JsonProcessingException {
        final var mapper = new ObjectMapper();
        return mapper.writeValueAsString(payload);
    }

    private void downloadHistoryFiles() {
        // Checking if Google Drive download enabled
        if (!properties.googleDrive().enabled()) {
            log.info("Skipped downloading history files from Google Drive!");
            return;
        }

        // Sending download request
        final var request = new DownloadGoogleDriveFilesRequest();
        request.setGoogleDriveFolderId(properties.googleDrive().folderId());
        request.setFileExtension(FILE_EXTENSION);
        request.setLocalDataDirectory(properties.dataDirectory());
        final var response = storageService.downloadGoogleDriveFiles(request);

        // Logging how many new files downloaded
        final var downloadedFiles = response
                .map(DownloadGoogleDriveFilesResponse::getDownloadedFiles)
                .orElse(List.of());
        log.info("New history files downloaded: {}", downloadedFiles.size());
    }

    private void uploadHistoryFiles() {
        // Checking if Google Drive upload enabled
        if (!properties.googleDrive().enabled()) {
            log.info("Skipped uploading history files to Google Drive!");
            return;
        }

        // Sending upload request
        final var request = new UploadLocalFilesRequest();
        request.setGoogleDriveFolderId(properties.googleDrive().folderId());
        request.setFileExtension(FILE_EXTENSION);
        request.setLocalDataDirectory(properties.dataDirectory());
        final var response = storageService.uploadLocalFiles(request);

        // Logging how many new files uploaded
        final var uploadedFiles = response
                .map(UploadLocalFilesResponse::getUploadedFiles)
                .orElse(List.of());
        log.info("History files uploaded: {}", uploadedFiles.size());
    }

}
