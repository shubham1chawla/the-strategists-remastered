package com.strategists.game.update.handler;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.EndUpdatePayload;

import lombok.val;

@Component
public class EndUpdateHandler extends AbstractUpdateHandler<EndUpdatePayload> {

	@Override
	public UpdateType getType() {
		return UpdateType.END;
	}

	@Override
	public void handle(Object returnValue, Object[] args) {
		// Winner player returned by the method
		val player = (Player) returnValue;

		// If no winner is declared, update the trends and avoid sending update
		if (Objects.isNull(player)) {
			updateTrends();
			return;
		}

		// Exporting game data if winner is declared
		exportGameData();

		// Persisting the activity and sending the update
		val activity = Activity.ofEnd(player.getUsername());
		sendUpdate(new EndUpdatePayload(saveActivity(activity), player));
	}

}