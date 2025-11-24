package com.strategists.game.configuration.properties;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "strategists.clean-up")
public record CleanUpConfigurationProperties(boolean enabled, @Positive int delay) {

    @AssertTrue(message = "Clean up delay should be more than 10 seconds!")
    boolean isDelayValid() {
        return !enabled || delay > 10000;
    }

    @NonNull
    @Override
    public String toString() {
        return "\n--------------------------------------------------" +
                "\nClean Up:" +
                "\n> Enabled: " + enabled +
                "\n> Delay (seconds): " + delay +
                "\n--------------------------------------------------";
    }

}
