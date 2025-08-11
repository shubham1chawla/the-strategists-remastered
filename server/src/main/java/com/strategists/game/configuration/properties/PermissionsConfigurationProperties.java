package com.strategists.game.configuration.properties;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "strategists.permissions")
public record PermissionsConfigurationProperties(
        @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties healthCheck,
        @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties googleRecaptcha,
        @DefaultValue @Valid ExternalAPIEndpointConfigurationProperties permissionGroup) {

}
