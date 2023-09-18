package com.strategists.game.activity.handler;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.strategists.game.activity.payload.EndUpdatePayload;
import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Player;

import lombok.val;

@Component
public class EndActivityHandler implements ActivityHandler<EndUpdatePayload> {

	@Override
	public Optional<EndUpdatePayload> apply(Object obj, Object[] args) {
		val player = (Player) obj;
		if (Objects.isNull(player)) {
			return Optional.empty();
		}

		val activity = Activity.ofEnd(player.getUsername());
		return Optional.of(new EndUpdatePayload(activity, player));
	}

	@Override
	public Type getType() {
		return Type.END;
	}

}
