package com.strategists.game.configuration.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.io.File;

@Validated
@ConfigurationProperties(prefix = "strategists.predictions")
public record PredictionsConfigurationProperties(boolean enabled,
                                                 @DefaultValue @Valid Legacy legacy,
                                                 @DefaultValue @Valid Strategies strategies,
                                                 @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties healthCheckApi,
                                                 @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties loadModelInfoApi,
                                                 @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties trainModelApi,
                                                 @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties inferModelApi) {

    public record Legacy(File dataDirectory,
                         @DefaultValue @Valid Export export,
                         @DefaultValue @Valid GoogleDrive googleDrive) {

        public record Export(boolean enabled) {
        }

        public record GoogleDrive(boolean enabled, String folderId) {

            @AssertTrue(message = "Predictions legacy data download enabled, but Google Drive folder ID not provided!")
            boolean isFolderIdValid() {
                return !enabled || StringUtils.hasText(folderId);
            }

        }

        public boolean enabled() {
            return export.enabled() || googleDrive.enabled();
        }

    }


    public record Strategies(boolean trainOnStartupEnabled,
                             boolean trainOnEndEnabled,
                             boolean modelInferenceEnabled) {
    }

    @AssertTrue(message = "Predictions enabled but health check API is disabled! Enable predictions' health check API!")
    boolean isHealthCheckAPIValid() {
        return !enabled || healthCheckApi.enabled();
    }

    @AssertTrue(message = "Predictions' load model info API is enabled but Predictions are disabled! Enable Predictions or disable load model info API!")
    boolean isLoadModelInfoAPIValid() {
        return !loadModelInfoApi.enabled() || enabled;
    }

    @AssertTrue(message = "Predictions' train model API is enabled but Predictions are disabled! Enable Predictions or disable train model API!")
    boolean isTrainModelAPIValid() {
        return !trainModelApi.enabled() || enabled;
    }

    @AssertTrue(message = "Predictions' infer model API is enabled but Predictions are disabled! Enable Predictions or disable infer model API!")
    boolean isInferModelAPIValid() {
        return !inferModelApi.enabled() || enabled;
    }

    @AssertTrue(message = "Predictions' legacy features (export or Google Drive download) enabled but Predictions is not enabled! " +
            "Either disable Predictions' legacy features or enable Predictions!")
    boolean isLegacyValid() {
        return !legacy.enabled() || enabled;
    }

    @NonNull
    @Override
    public String toString() {
        return "\n--------------------------------------------------" +
                "\nPredictions:" +
                "\n> Enabled: " + enabled +
                "\n> Health Check API: " +
                "\n\t> Enabled: " + healthCheckApi.enabled() +
                "\n\t> Endpoint: " + healthCheckApi.endpoint() +
                "\n> Load Model Info API: " +
                "\n\t> Enabled: " + loadModelInfoApi.enabled() +
                "\n\t> Endpoint: " + loadModelInfoApi.endpoint() +
                "\n> Train Model API: " +
                "\n\t> Enabled: " + trainModelApi.enabled() +
                "\n\t> Endpoint: " + trainModelApi.endpoint() +
                "\n> Infer Model API: " +
                "\n\t> Enabled: " + inferModelApi.enabled() +
                "\n\t> Endpoint: " + inferModelApi.endpoint() +
                "\n> Strategies: " +
                "\n\t> Train On Start-up Enabled: " + strategies.trainOnStartupEnabled() +
                "\n\t> Train On End Enabled: " + strategies.trainOnEndEnabled() +
                "\n\t> Model Inference Enabled: " + strategies.modelInferenceEnabled() +
                "\n> Legacy: " +
                "\n\t> Enabled: " + legacy.enabled() +
                "\n\t> Data Directory: " + legacy.dataDirectory() +
                "\n\t> Export: " +
                "\n\t\t> Enabled: " + legacy.export().enabled() +
                "\n\t> Google Drive Download: " +
                "\n\t\t> Enabled: " + legacy.googleDrive().enabled() +
                "\n\t\t> Folder ID: " + legacy.googleDrive().folderId() +
                "\n--------------------------------------------------";
    }

}
