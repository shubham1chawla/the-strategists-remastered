package com.strategists.game.activity.handler;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.strategists.game.activity.payload.JoinUpdatePayload;
import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Player;

import lombok.val;

@Component
public class JoinActivityHandler implements ActivityHandler<JoinUpdatePayload> {

	@Override
	public Optional<JoinUpdatePayload> apply(Object obj, Object[] args) {
		val player = (Player) obj;

		val activity = Activity.ofJoin(player.getUsername(), player.getCash());
		return Optional.of(new JoinUpdatePayload(activity, player));
	}

	@Override
	public Type getType() {
		return Type.JOIN;
	}

}
