package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.service.PredictionService.Prediction;
import com.strategists.game.update.UpdateType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PredictionUpdatePayload implements UpdatePayload<Prediction> {

	private Activity activity;
	private Prediction payload;

	@Override
	public UpdateType getType() {
		return UpdateType.PREDICTION;
	}

}
