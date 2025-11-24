package com.strategists.game.configuration.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "strategists.storage")
public record StorageConfigurationProperties(boolean enabled,
                                             @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties healthCheckApi,
                                             @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties downloadApi,
                                             @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties uploadApi) {

    @AssertTrue(message = "Storage enabled but health check API is disabled! Enable storage's health check API!")
    boolean isHealthCheckAPIValid() {
        return !enabled || healthCheckApi.enabled();
    }

    @AssertTrue(message = "Storage's download API is enabled but Storage is disabled! Enable Storage or disable download API!")
    boolean isDownloadAPIValid() {
        return !downloadApi.enabled() || enabled;
    }

    @AssertTrue(message = "Storage's upload API is enabled but Storage is disabled! Enable Storage or disable upload API!")
    boolean isUploadAPIValid() {
        return !uploadApi.enabled() || enabled;
    }

    @NonNull
    @Override
    public String toString() {
        return "\n--------------------------------------------------" +
                "\nStorage:" +
                "\n> Enabled: " + enabled +
                "\n> Health Check API: " +
                "\n\t> Enabled: " + healthCheckApi.enabled() +
                "\n\t> Endpoint: " + healthCheckApi.endpoint() +
                "\n> Download API: " +
                "\n\t> Enabled: " + downloadApi.enabled() +
                "\n\t> Endpoint: " + downloadApi.endpoint() +
                "\n> Upload API: " +
                "\n\t> Enabled: " + uploadApi.enabled() +
                "\n\t> Endpoint: " + uploadApi.endpoint() +
                "\n--------------------------------------------------";
    }

}
