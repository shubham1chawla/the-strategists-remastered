package com.strategists.game.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Data
@Log4j2
public class GameMap {

    private static ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();

    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @Positive
    private Double playerBaseCash;

    @Min(value = 2, message = "Dice size should be >=2!")
    @Max(value = 8, message = "Dice size should be <=8!")
    private Integer diceSize;

    @Positive
    private Double rentFactor;

    @NotEmpty
    private List<Land> lands;

    public static GameMap from(File json) {
        final var mapper = new ObjectMapper();

        // Loading game map from file
        GameMap gameMap;
        try {
            gameMap = mapper.readValue(json, GameMap.class);
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
        }

        // Checking if validation passed
        final var validator = FACTORY.getValidator();
        final var violations = validator.validate(gameMap);
        if (!CollectionUtils.isEmpty(violations)) {
            final var message = String.join("\n", violations.stream().map(ConstraintViolation::getMessage).toList());
            throw new ValidationException(message);
        }
        return gameMap;
    }

    @AssertTrue(message = "Rent factor should be >=0.1 and <=0.5!")
    boolean isRentFactorValid() {
        return rentFactor >= 0.1 && rentFactor <= 0.5;
    }

}
