package com.strategists.game.update;

import java.util.List;
import java.util.Map;

import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;

public class InvestmentUpdatePayload extends AbstractUpdatePayload<Map<String, Object>> {

	public InvestmentUpdatePayload(Land land, List<Player> players) {
		super(Type.INVEST, Map.of("land", land, "players", players));
	}

}
