package com.strategists.game.activity.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;

public class EndUpdatePayload extends AbstractUpdatePayload<Player> {

	public EndUpdatePayload(Activity activity, Player player) {
		super(activity, player);
	}

}
