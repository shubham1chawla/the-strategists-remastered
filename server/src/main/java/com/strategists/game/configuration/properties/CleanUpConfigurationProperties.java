package com.strategists.game.configuration.properties;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "strategists.clean-up")
@ConditionalOnProperty(name = "strategists.clean-up.enabled", havingValue = "true")
public record CleanUpConfigurationProperties(@Positive int delay) {

    @AssertTrue(message = "Clean up delay should be more than 10 seconds!")
    boolean isDelayValid() {
        return delay > 10000;
    }

}
