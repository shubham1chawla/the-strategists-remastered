package com.strategists.game.update.handler;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.MoveUpdatePayload;

import lombok.val;

@Component
public class MoveUpdateHandler extends AbstractUpdateHandler<MoveUpdatePayload> {

	@Override
	public UpdateType getType() {
		return UpdateType.MOVE;
	}

	@Override
	public void handle(Object returnValue, Object[] args) {
		// Moving player and move amount from argument with associated land returned
		val player = (Player) args[0];
		val move = (int) args[1];
		val land = (Land) returnValue;

		// Persisting the activity and sending the update
		val activity = Activity.ofMove(player.getUsername(), move, land.getName());
		sendUpdate(new MoveUpdatePayload(saveActivity(activity), player));
	}

}
