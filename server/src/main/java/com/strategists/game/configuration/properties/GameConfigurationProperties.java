package com.strategists.game.configuration.properties;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "strategists.game")
public record GameConfigurationProperties(@NotBlank String defaultGameMapFilePath,
                                          @Min(value = 2, message = "Code length should be >=2!")
                                          @Max(value = 6, message = "Code length should be <=6!")
                                          int codeLength,
                                          @Min(value = 2, message = "Minimum players count should be >=2!")
                                          int minPlayersCount,
                                          @Min(value = 2, message = "Maximum players count should be >=2!")
                                          @Max(value = 6, message = "Maximum players count should be <=6!")
                                          int maxPlayersCount) {

    @AssertTrue(message = "Minimum players count should be <= Maximum players count!")
    boolean isMinMaxPlayersCountValid() {
        return minPlayersCount <= maxPlayersCount;
    }

    @NonNull
    @Override
    public String toString() {
        return "\n--------------------------------------------------" +
                "\nGame:" +
                "\n> Default Game Map File Path: " + defaultGameMapFilePath +
                "\n> Code Length: " + codeLength +
                "\n> Min Players Count: " + minPlayersCount +
                "\n> Max Players Count: " + maxPlayersCount +
                "\n--------------------------------------------------";
    }

}
