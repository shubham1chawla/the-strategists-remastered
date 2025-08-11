package com.strategists.game.configuration.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record ExternalAPIEndpointConfigurationProperties(boolean bypassForTesting, @NotBlank String apiEndpoint) {

}
