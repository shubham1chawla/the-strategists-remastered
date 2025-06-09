package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Prediction;
import com.strategists.game.update.UpdateType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PredictionUpdatePayload implements UpdatePayload<List<Prediction>> {

    private Activity activity;
    private List<Prediction> payload;

    @Override
    public UpdateType getType() {
        return UpdateType.PREDICTION;
    }

}
