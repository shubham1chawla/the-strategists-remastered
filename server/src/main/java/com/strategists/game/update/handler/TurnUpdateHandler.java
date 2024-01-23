package com.strategists.game.update.handler;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.TurnUpdatePayload;

import lombok.val;

@Component
public class TurnUpdateHandler extends AbstractUpdateHandler<TurnUpdatePayload> {

	@Override
	public UpdateType getType() {
		return UpdateType.TURN;
	}

	@Override
	public void handle(Object returnValue, Object[] args) {
		// Checking if valid current player exists
		if (Objects.isNull(returnValue)) {
			return;
		}
		val currentPlayer = (Player) returnValue;
		val previousPlayer = (Player) args[0];

		// Running prediction model
		if (!previousPlayer.isBankrupt()) {
			executePredictionModelAsync(previousPlayer);
		}

		// Persisting the activity and sending the update
		val activity = Activity.ofTurn(previousPlayer.getUsername(), currentPlayer.getUsername());
		sendUpdate(new TurnUpdatePayload(saveActivity(activity), currentPlayer, previousPlayer));
	}

}
