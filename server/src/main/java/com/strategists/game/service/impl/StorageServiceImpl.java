package com.strategists.game.service.impl;

import com.strategists.game.configuration.properties.ExternalAPIEndpointConfigurationProperties;
import com.strategists.game.configuration.properties.StorageConfigurationProperties;
import com.strategists.game.request.DownloadGoogleDriveFilesRequest;
import com.strategists.game.request.UploadLocalFilesRequest;
import com.strategists.game.response.DownloadGoogleDriveFilesResponse;
import com.strategists.game.response.UploadLocalFilesResponse;
import com.strategists.game.service.StorageService;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Log4j2
@Service
public class StorageServiceImpl extends AbstractExternalService implements StorageService {

    @Autowired
    private StorageConfigurationProperties properties;

    @Override
    public synchronized Optional<DownloadGoogleDriveFilesResponse> downloadGoogleDriveFiles(DownloadGoogleDriveFilesRequest request) {
        // Google Drive API fails if multiple requests try to the same resource. Noticed [SSL] record layer failure (_ssl.c:2559)
        // Stackoverflow article (read comments) - https://stackoverflow.com/questions/77964507/google-drive-python-sdk-throwing-ssl-errors

        log.info("Downloading files from Google Drive...");

        // Checking if API call is by-passed!
        if (properties.download().bypassForTesting()) {
            log.warn("Bypassing Google Drive download for testing! This should only happen for local testing!");
            return Optional.empty();
        }

        // Downloading files
        try {
            final var restTemplate = new RestTemplate();
            final var response = restTemplate.postForObject(properties.download().apiEndpoint(), request, DownloadGoogleDriveFilesResponse.class);

            log.info("Downloaded Google Drive files!");
            return Optional.ofNullable(response);
        } catch (Exception ex) {
            log.error("Unable to download files from Google Drive! Reason: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public synchronized Optional<UploadLocalFilesResponse> uploadLocalFiles(UploadLocalFilesRequest request) {
        // Google Drive API fails if multiple requests try to the same resource. Noticed [SSL] record layer failure (_ssl.c:2559)
        // Stackoverflow article (read comments) - https://stackoverflow.com/questions/77964507/google-drive-python-sdk-throwing-ssl-errors

        log.info("Uploading files to Google Drive...");

        // Checking if API call is by-passed!
        if (properties.upload().bypassForTesting()) {
            log.warn("Bypassing Google Drive upload for testing! This should only happen for local testing!");
            return Optional.empty();
        }

        // Uploading files
        try {
            final var restTemplate = new RestTemplate();
            final var response = restTemplate.postForObject(properties.upload().apiEndpoint(), request, UploadLocalFilesResponse.class);

            log.info("Uploaded local files!");
            return Optional.ofNullable(response);
        } catch (Exception ex) {
            log.error("Unable to upload files to Google Drive! Reason: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    protected ExternalAPIEndpointConfigurationProperties getHealthCheck() {
        return properties.healthCheck();
    }

    @Override
    protected String getExternalServiceName() {
        return "strategists-storage";
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
