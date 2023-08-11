package com.strategists.game.update;

import java.util.Collection;
import java.util.Map;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;

public class BankruptcyUpdatePayload extends AbstractUpdatePayload<Map<String, Object>> {

	public BankruptcyUpdatePayload(Activity activity, Collection<Land> lands, Collection<Player> players) {
		super(activity, Map.of("lands", lands, "players", players));
	}

}
