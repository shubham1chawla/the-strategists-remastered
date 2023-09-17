package com.strategists.game.activity.payload;

import java.util.List;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;

public class RentUpdatePayload extends AbstractUpdatePayload<List<Player>> {

	public RentUpdatePayload(Activity activity, List<Player> players) {
		super(activity, players);
	}

}
