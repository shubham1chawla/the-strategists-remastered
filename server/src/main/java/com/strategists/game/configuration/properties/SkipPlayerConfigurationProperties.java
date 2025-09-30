package com.strategists.game.configuration.properties;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "strategists.skip-player")
@ConditionalOnProperty(name = "strategists.skip-player.enabled", havingValue = "true")
public record SkipPlayerConfigurationProperties(@Positive int allowedCount, @Positive int timeout) {

    @AssertTrue(message = "Player's allowed skips count should be >2 and <=5!")
    boolean isAllowedCountValid() {
        return allowedCount > 2 && allowedCount <= 5;
    }

    @AssertTrue(message = "Player's turn timeout should be more than 10 seconds!")
    boolean isTimeoutValid() {
        return timeout > 10000;
    }

}
