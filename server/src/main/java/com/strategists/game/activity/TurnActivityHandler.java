package com.strategists.game.activity;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Player;
import com.strategists.game.update.TurnUpdatePayload;

import lombok.val;

@Component
public class TurnActivityHandler implements ActivityHandler<TurnUpdatePayload> {

	@Override
	public Optional<TurnUpdatePayload> apply(Object obj, Object[] args) {
		if (Objects.isNull(obj)) {
			return Optional.empty();
		}
		val curr = (Player) obj;
		val prev = (Player) args[0];

		val activity = Activity.ofTurn(prev.getUsername(), curr.getUsername());
		return Optional.of(new TurnUpdatePayload(activity, curr, prev));
	}

	@Override
	public Type getType() {
		return Type.TURN;
	}

}
