package com.strategists.game.update.handler;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.StartUpdatePayload;

import lombok.val;

@Component
public class StartUpdateHandler extends AbstractUpdateHandler<StartUpdatePayload> {

	@Override
	public UpdateType getType() {
		return UpdateType.START;
	}

	@Override
	public void handle(Object returnValue, Object[] args) {
		// Starting player returned from the method
		val player = (Player) returnValue;

		// Training the prediction model
		trainPredictionModelAsync(false);

		// Persisting the activity and sending the update
		val activity = Activity.ofStart(getAdminUsername(), player.getUsername());
		sendUpdate(new StartUpdatePayload(saveActivity(activity), player));
	}

}
