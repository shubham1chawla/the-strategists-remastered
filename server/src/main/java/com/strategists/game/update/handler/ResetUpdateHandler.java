package com.strategists.game.update.handler;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.ResetUpdatePayload;

@Component
public class ResetUpdateHandler extends AbstractUpdateHandler<ResetUpdatePayload> {

	@Override
	public UpdateType getType() {
		return UpdateType.RESET;
	}

	@Override
	public void handle(Object returnValue, Object[] args) {
		// Resetting activities and trends
		reset();

		// Sending unsaved activity
		sendUpdate(new ResetUpdatePayload(Activity.ofReset(getAdminUsername())));
	}

}
