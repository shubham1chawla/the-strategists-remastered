package com.strategists.game.service.impl;

import com.strategists.game.configuration.properties.StorageConfigurationProperties;
import com.strategists.game.request.DownloadGoogleDriveFilesRequest;
import com.strategists.game.request.UploadLocalFilesRequest;
import com.strategists.game.response.DownloadGoogleDriveFilesResponse;
import com.strategists.game.response.UploadLocalFilesResponse;
import com.strategists.game.service.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.util.Optional;

@Log4j2
@Service
public class StorageServiceImpl extends AbstractExternalService implements StorageService {

    @Autowired
    private StorageConfigurationProperties properties;

    public StorageServiceImpl() {
        super("strategists-storage", log);
    }

    @PostConstruct
    public void setup() throws InterruptedException, ConnectException {
        waitUntilReady(properties.healthCheck());
    }

    @Override
    public Optional<DownloadGoogleDriveFilesResponse> downloadGoogleDriveFiles(DownloadGoogleDriveFilesRequest request) {
        log.info("Downloading files from Google Drive...");

        // Checking if API call is by-passed!
        if (properties.download().bypassForTesting()) {
            log.warn("Bypassing Google Drive download for testing! This should only happen for local testing!");
            return Optional.empty();
        }

        // Downloading files
        try {
            var restTemplate = new RestTemplate();
            var response = restTemplate.postForObject(properties.download().apiEndpoint(), request, DownloadGoogleDriveFilesResponse.class);

            log.info("Downloaded Google Drive files!");
            return Optional.ofNullable(response);
        } catch (Exception ex) {
            log.error("Unable to download files from Google Drive! Reason: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<UploadLocalFilesResponse> uploadLocalFiles(UploadLocalFilesRequest request) {
        log.info("Uploading files to Google Drive...");

        // Checking if API call is by-passed!
        if (properties.upload().bypassForTesting()) {
            log.warn("Bypassing Google Drive upload for testing! This should only happen for local testing!");
            return Optional.empty();
        }

        // Uploading files
        try {
            var restTemplate = new RestTemplate();
            var response = restTemplate.postForObject(properties.upload().apiEndpoint(), request, UploadLocalFilesResponse.class);

            log.info("Uploaded local files!");
            return Optional.ofNullable(response);
        } catch (Exception ex) {
            log.error("Unable to upload files to Google Drive! Reason: {}", ex.getMessage());
            return Optional.empty();
        }
    }

}
