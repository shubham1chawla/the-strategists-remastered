package com.strategists.game.activity;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.update.MoveUpdatePayload;

import lombok.val;

@Component
public class MoveActivityHandler implements ActivityHandler<MoveUpdatePayload> {

	@Override
	public Optional<MoveUpdatePayload> apply(Object obj, Object[] args) {
		val player = (Player) args[0];
		val move = (int) args[1];
		val land = (Land) obj;

		val activity = Activity.ofMove(player.getUsername(), move, land.getName());
		return Optional.of(new MoveUpdatePayload(activity, player));
	}

	@Override
	public Type getType() {
		return Type.MOVE;
	}

}
