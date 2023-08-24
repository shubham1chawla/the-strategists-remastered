package com.strategists.game.update;

import java.util.List;
import java.util.Map;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;

public class InvestUpdatePayload extends AbstractUpdatePayload<Map<String, Object>> {

	public InvestUpdatePayload(Activity activity, Land land, List<Player> players) {
		super(activity, Map.of("land", land, "players", players));
	}

}
