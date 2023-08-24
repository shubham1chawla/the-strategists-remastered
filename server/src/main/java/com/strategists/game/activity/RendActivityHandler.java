package com.strategists.game.activity;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Rent;
import com.strategists.game.update.RentUpdatePayload;

import lombok.val;

@Component
public class RendActivityHandler implements ActivityHandler<RentUpdatePayload> {

	@Override
	public Optional<RentUpdatePayload> apply(Object obj, Object[] args) {
		val rent = (Rent) args[0];
		val source = rent.getSourcePlayer();
		val target = rent.getTargetPlayer();
		val land = rent.getLand();
		val rentAmount = rent.getRentAmount();

		// Creating activity for rent
		val activity = Activity.ofRent(source.getUsername(), rentAmount, target.getUsername(), land.getName());

		return Optional.of(new RentUpdatePayload(activity, List.of(source, target)));
	}

	@Override
	public Type getType() {
		return Type.RENT;
	}

}
