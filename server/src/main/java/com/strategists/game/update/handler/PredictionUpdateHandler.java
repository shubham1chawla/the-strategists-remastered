package com.strategists.game.update.handler;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.service.PredictionService.Prediction;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.PredictionUpdatePayload;

import lombok.val;

@Component
public class PredictionUpdateHandler extends AbstractUpdateHandler<PredictionUpdatePayload> {

	@Override
	public UpdateType getType() {
		return UpdateType.PREDICTION;
	}

	@Override
	public void handle(Object returnValue, Object[] args) {
		// Player from the argument and prediction returned
		val player = (Player) args[0];
		val prediction = (Prediction) returnValue;

		// Checking if prediction result is valid
		if (Prediction.UNKNOWN.equals(prediction)) {
			return;
		}

		// Persisting the activity and sending the update
		val activity = Activity.ofPrediction(getAdminUsername(), player.getUsername(), prediction.name());
		sendUpdate(new PredictionUpdatePayload(saveActivity(activity), prediction));
	}

}
