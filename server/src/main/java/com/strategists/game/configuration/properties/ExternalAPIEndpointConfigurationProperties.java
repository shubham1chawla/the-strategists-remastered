package com.strategists.game.configuration.properties;

import jakarta.validation.constraints.AssertTrue;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.validation.annotation.Validated;

@Validated
public record ExternalAPIEndpointConfigurationProperties(boolean enabled, String endpoint) {

    @AssertTrue(message = "API endpoint is not valid!")
    public boolean isAPIEndpointValid() {
        final var validator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
        return !enabled || validator.isValid(endpoint);
    }

}
