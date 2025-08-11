package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.PlayerPrediction;
import com.strategists.game.update.UpdateType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PredictionUpdatePayload implements UpdatePayload<List<PlayerPrediction>> {

    private Activity activity;
    private List<PlayerPrediction> payload;

    @Override
    public UpdateType getType() {
        return UpdateType.PREDICTION;
    }

}
