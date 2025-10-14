package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.PlayerPrediction;
import com.strategists.game.update.UpdateType;
import lombok.Getter;

import java.util.List;

@Getter
public class PredictionUpdatePayload implements UpdatePayload<List<PlayerPrediction>> {

    private final Long timestamp = System.currentTimeMillis();
    private final UpdateType type = UpdateType.PREDICTION;
    private final String gameCode;
    private final Integer gameStep;
    private final Activity activity;
    private final List<PlayerPrediction> payload;

    public PredictionUpdatePayload(Activity activity, List<PlayerPrediction> playerPredictions) {
        final var game = activity.getGame();
        this.gameCode = game.getCode();
        this.gameStep = game.getCurrentStep();
        this.activity = activity;
        this.payload = playerPredictions;
    }

}
