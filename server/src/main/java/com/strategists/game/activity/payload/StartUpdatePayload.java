package com.strategists.game.activity.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;

public class StartUpdatePayload extends AbstractUpdatePayload<Player> {

	public StartUpdatePayload(Activity activity, Player player) {
		super(activity, player);
	}

}
