package com.strategists.game.update.handler;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Game;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.ResetUpdatePayload;

import lombok.val;

@Component
public class ResetUpdateHandler extends AbstractUpdateHandler<ResetUpdatePayload> {

	@Override
	public UpdateType getType() {
		return UpdateType.RESET;
	}

	@Override
	@Transactional
	public void handle(Object returnValue, Object[] args) {
		// Game instance from argument
		val game = (Game) args[0];

		// Resetting activities and trends
		reset(game);

		// Sending unsaved activity
		sendUpdate(game, new ResetUpdatePayload(Activity.ofReset(game)));
	}

}
