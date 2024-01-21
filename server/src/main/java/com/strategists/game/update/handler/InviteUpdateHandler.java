package com.strategists.game.update.handler;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.InviteUpdatePayload;

import lombok.val;

@Component
public class InviteUpdateHandler extends AbstractUpdateHandler<InviteUpdatePayload> {

	@Override
	public UpdateType getType() {
		return UpdateType.INVITE;
	}

	@Override
	public void handle(Object returnValue, Object[] args) {
		// Invited player returned by the method
		val player = (Player) returnValue;

		// Persisting the activity and sending the update
		val activity = Activity.ofInvite(getAdminUsername(), player.getEmail());
		sendUpdate(new InviteUpdatePayload(saveActivity(activity), player));
	}

}
