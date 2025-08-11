package com.strategists.game.configuration.properties;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "strategists.storage")
public record StorageConfigurationProperties(
        @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties healthCheck,
        @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties download,
        @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties upload) {
}
