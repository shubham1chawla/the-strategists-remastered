package com.strategists.game.activity.payload;

import java.util.List;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;

public class TurnUpdatePayload extends AbstractUpdatePayload<List<Player>> {

	public TurnUpdatePayload(Activity activity, Player curr, Player prev) {
		super(activity, List.of(curr, prev));
	}

}
