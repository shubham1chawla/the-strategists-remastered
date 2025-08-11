package com.strategists.game.configuration.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.io.File;

@Validated
@ConfigurationProperties(prefix = "strategists.predictions")
@ConditionalOnProperty(name = "strategists.predictions.enabled", havingValue = "true")
public record PredictionsConfigurationProperties(File dataDirectory,
                                                 @DefaultValue @Valid GoogleDrive googleDrive,
                                                 @DefaultValue @Valid Strategies strategies,
                                                 @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties healthCheck,
                                                 @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties getModel,
                                                 @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties trainModel,
                                                 @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties inferModel) {

    public record GoogleDrive(@NotBlank String downloadFolderId, @NotBlank String uploadFolderId) {
    }

    public record Strategies(boolean trainOnStartupEnabled,
                             boolean trainOnEndEnabled,
                             boolean dataExportEnabled,
                             boolean dataIntegrityValidationEnabled,
                             boolean modelInferenceEnabled) {
    }

}
