package com.strategists.game.configuration.properties;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "strategists.game")
public record GameConfigurationProperties(@NotBlank String defaultMap,
                                          @Positive int diceSize,
                                          @Positive double rentFactor,
                                          @Positive int codeLength,
                                          @Positive int minPlayersCount,
                                          @Positive int maxPlayersCount) {

    @AssertTrue(message = "Dice size should be >1 and <=8!")
    boolean isDiceSizeValid() {
        return diceSize > 1 && diceSize <= 8;
    }

    @AssertTrue(message = "Rent factor should be >=0.1 and <=0.5!")
    boolean isRentFactorValid() {
        return rentFactor >= 0.1 && rentFactor <= 0.5;
    }

    @AssertTrue(message = "Code length should be >1 and <=6!")
    boolean isCodeLengthValid() {
        return codeLength > 1 && codeLength <= 6;
    }

    @AssertTrue(message = "Minimum players count should be >1!")
    boolean isMinPlayersCountValid() {
        return minPlayersCount > 1;
    }

    @AssertTrue(message = "Maximum players count should be >2 and <=8!")
    boolean isMaxPlayersCountValid() {
        return maxPlayersCount > 2 && maxPlayersCount <= 8;
    }

    @AssertTrue(message = "Minimum players count should be <= Maximum players count!")
    boolean isMinMaxPlayersCountValid() {
        return minPlayersCount <= maxPlayersCount;
    }

    @NonNull
    @Override
    public String toString() {
        return "\n--------------------------------------------------" +
                "\nGame:" +
                "\n> Default Map: " + defaultMap +
                "\n> Dice Size: " + diceSize +
                "\n> Rent Factor: " + rentFactor +
                "\n> Code Length: " + codeLength +
                "\n> Min Players Count: " + minPlayersCount +
                "\n> Max Players Count: " + maxPlayersCount +
                "\n--------------------------------------------------";
    }

}
