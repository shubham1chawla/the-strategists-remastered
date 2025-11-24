package com.strategists.game.configuration.properties;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "strategists.skip-player")
public record SkipPlayerConfigurationProperties(boolean enabled, @Positive int allowedCount, @Positive int timeout) {

    @AssertTrue(message = "Player's allowed skips count should be >2 and <=5!")
    boolean isAllowedCountValid() {
        return !enabled || (allowedCount > 2 && allowedCount <= 5);
    }

    @AssertTrue(message = "Player's turn timeout should be more than 10 seconds!")
    boolean isTimeoutValid() {
        return !enabled || timeout > 10000;
    }

    @NonNull
    @Override
    public String toString() {
        return "\n--------------------------------------------------" +
                "\nSkip Player:" +
                "\n> Enabled: " + enabled +
                "\n> Allowed Count: " + allowedCount +
                "\n> Timeout (seconds): " + timeout +
                "\n--------------------------------------------------";
    }

}
