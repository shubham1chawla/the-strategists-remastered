package com.strategists.game.update;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;

public class MoveUpdatePayload extends AbstractUpdatePayload<Player> {

	public MoveUpdatePayload(Activity activity, Player player) {
		super(activity, player);
	}

}
