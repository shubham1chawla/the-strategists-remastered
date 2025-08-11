package com.strategists.game.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.strategists.game.entity.PlayerPrediction;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PlayerPredictionResponse {

    private String gameCode;
    private int playerId;
    private PlayerPrediction.Prediction prediction;
    private double winnerProbability;
    private double bankruptProbability;

}
