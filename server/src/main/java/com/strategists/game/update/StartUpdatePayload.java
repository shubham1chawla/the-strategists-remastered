package com.strategists.game.update;

import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Player;

public class StartUpdatePayload extends AbstractUpdatePayload<Player> {

	public StartUpdatePayload(Player player) {
		super(Type.START, player);
	}

}
