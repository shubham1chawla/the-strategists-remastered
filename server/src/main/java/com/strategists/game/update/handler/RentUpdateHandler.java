package com.strategists.game.update.handler;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Rent;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.RentUpdatePayload;

import lombok.val;

@Component
public class RentUpdateHandler extends AbstractUpdateHandler<RentUpdatePayload> {

	@Override
	public UpdateType getType() {
		return UpdateType.RENT;
	}

	@Override
	public void handle(Object returnValue, Object[] args) {
		// Source player, target player, and other information from argument
		val rent = (Rent) args[0];
		val source = rent.getSourcePlayer();
		val target = rent.getTargetPlayer();

		// Persisting the activity and sending the update
		val activity = Activity.ofRent(rent);
		sendUpdate(source.getGame(), new RentUpdatePayload(saveActivity(activity), source, target));
	}

}
