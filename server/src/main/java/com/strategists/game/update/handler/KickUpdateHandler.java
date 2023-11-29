package com.strategists.game.update.handler;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
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
		// Persisting the activity and sending the update
		val activity = Activity.ofKick(getAdminUsername(), (String) args[0]);
		sendUpdate(new KickUpdatePayload(saveActivity(activity), (String) args[0]));
	}

}
