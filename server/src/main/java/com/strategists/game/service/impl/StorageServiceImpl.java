package com.strategists.game.service.impl;

import com.strategists.game.configuration.properties.ExternalAPIEndpointConfigurationProperties;
import com.strategists.game.configuration.properties.StorageConfigurationProperties;
import com.strategists.game.request.DownloadGoogleDriveFilesRequest;
import com.strategists.game.request.UploadLocalFilesRequest;
import com.strategists.game.response.DownloadGoogleDriveFilesResponse;
import com.strategists.game.response.UploadLocalFilesResponse;
import com.strategists.game.service.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Log4j2
@Service
@ConditionalOnProperty(name = "strategists.storage.enabled", havingValue = "true")
public class StorageServiceImpl extends AbstractExternalService implements StorageService {

    @Autowired
    private StorageConfigurationProperties properties;

    @PostConstruct
    public void setup() {
        log.info(properties);
    }

    @Override
    public synchronized Optional<DownloadGoogleDriveFilesResponse> downloadGoogleDriveFiles(DownloadGoogleDriveFilesRequest request) {
        // Google Drive API fails if multiple requests try to the same resource. Noticed [SSL] record layer failure (_ssl.c:2559)
        // Stackoverflow article (read comments) - https://stackoverflow.com/questions/77964507/google-drive-python-sdk-throwing-ssl-errors

        // Checking if Google Drive download is enabled
        if (!properties.downloadApi().enabled()) {
            log.warn("Skipping Google Drive download! Assuming all the files are already downloaded manually.");
            return Optional.empty();
        }

        // Downloading files
        log.info("Downloading files from Google Drive...");
        try {
            final var restTemplate = new RestTemplate();
            final var response = restTemplate.postForObject(properties.downloadApi().endpoint(), request, DownloadGoogleDriveFilesResponse.class);

            log.info("Downloaded Google Drive files! Response: {}", response);
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

        // Checking if Google Drive upload is enabled
        if (!properties.uploadApi().enabled()) {
            log.warn("Skipping Google Drive upload! Assuming all the files are already uploaded manually.");
            return Optional.empty();
        }

        // Uploading files
        log.info("Uploading files to Google Drive...");
        try {
            final var restTemplate = new RestTemplate();
            final var response = restTemplate.postForObject(properties.uploadApi().endpoint(), request, UploadLocalFilesResponse.class);

            log.info("Uploaded local files! Response: {}", response);
            return Optional.ofNullable(response);
        } catch (Exception ex) {
            log.error("Unable to upload files to Google Drive! Reason: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    @Override
    protected ExternalAPIEndpointConfigurationProperties getHealthCheckApi() {
        return properties.healthCheckApi();
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
