package com.strategists.game.update.handler;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.JoinUpdatePayload;

import lombok.val;

@Component
public class JoinUpdateHandler extends AbstractUpdateHandler<JoinUpdatePayload> {

	@Override
	public UpdateType getType() {
		return UpdateType.JOIN;
	}

	@Override
	public void handle(Object returnValue, Object[] args) {
		// Joined player returned by the method
		val player = (Player) returnValue;

		// Persisting the activity and sending the update
		val activity = Activity.ofJoin(player);
		sendUpdate(player.getGame(), new JoinUpdatePayload(saveActivity(activity), player));
	}

}
