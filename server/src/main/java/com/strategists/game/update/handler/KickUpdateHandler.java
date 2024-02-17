package com.strategists.game.update.handler;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.KickUpdatePayload;

import lombok.val;

@Component
public class KickUpdateHandler extends AbstractUpdateHandler<KickUpdatePayload> {

	@Override
	public UpdateType getType() {
		return UpdateType.KICK;
	}

	@Override
	public void handle(Object returnValue, Object[] args) {
		val player = (Player) returnValue;
		if (Objects.isNull(player)) {
			return;
		}

		// Persisting the activity and sending the update
		val activity = Activity.ofKick(player);
		sendUpdate(player.getGame(), new KickUpdatePayload(saveActivity(activity), player.getId()));
	}

}
