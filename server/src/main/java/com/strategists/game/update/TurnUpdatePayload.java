package com.strategists.game.update;

import java.util.List;

import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Player;

public class TurnUpdatePayload extends AbstractUpdatePayload<List<Player>> {

	public TurnUpdatePayload(Player curr, Player prev) {
		super(Type.TURN, List.of(curr, prev));
	}

}
