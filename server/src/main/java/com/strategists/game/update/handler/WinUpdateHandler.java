package com.strategists.game.update.handler;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.WinUpdatePayload;

import lombok.val;

@Component
public class WinUpdateHandler extends AbstractUpdateHandler<WinUpdatePayload> {

	@Override
	public UpdateType getType() {
		return UpdateType.WIN;
	}

	@Override
	public void handle(Object returnValue, Object[] args) {
		// Winner player returned by the method and game is passed in argument
		val player = (Player) returnValue;
		val game = (Game) args[0];

		// If no winner is declared, avoid sending update
		if (Objects.isNull(player)) {

			// Scheduling player skip task
			scheduleSkipPlayerTask(game);

			return;
		}

		// Exporting data and training the prediction model
		trainPredictionModelAsync(game);

		// Persisting the activity and sending the update
		val activity = Activity.ofWin(player);
		sendUpdate(game, new WinUpdatePayload(saveActivity(activity), player));

		// Removing previously scheduled task
		unscheduleSkipPlayerTask(game);
	}

}
