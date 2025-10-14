package com.strategists.game.configuration.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.io.File;

@Validated
@ConfigurationProperties(prefix = "strategists.predictions")
@ConditionalOnProperty(name = "strategists.predictions.enabled", havingValue = "true")
public record PredictionsConfigurationProperties(@DefaultValue @Valid Legacy legacy,
                                                 @DefaultValue @Valid Strategies strategies,
                                                 @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties healthCheck,
                                                 @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties getModel,
                                                 @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties trainModel,
                                                 @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties inferModel) {

    public record Legacy(File dataDirectory,
                         @DefaultValue @Valid Export export,
                         @DefaultValue @Valid GoogleDrive googleDrive) {

        public record Export(boolean enabled) {
        }

        public record GoogleDrive(boolean enabled, String folderId) {

            @AssertTrue(message = "Predictions legacy data download enabled, but Google Drive folder ID not provided!")
            public boolean isFolderIdValid() {
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

}
