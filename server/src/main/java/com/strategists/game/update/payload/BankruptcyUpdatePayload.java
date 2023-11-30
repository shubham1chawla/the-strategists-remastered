package com.strategists.game.update.payload;

import java.util.Collection;
import java.util.Map;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;

import lombok.Getter;

@Getter
public class BankruptcyUpdatePayload implements UpdatePayload<Map<String, Object>> {

	private Activity activity;
	private Map<String, Object> payload;

	public BankruptcyUpdatePayload(Activity activity, Collection<Land> lands, Collection<Player> players) {
		this.activity = activity;
		this.payload = Map.of("lands", lands, "players", players);
	}

	@Override
	public UpdateType getType() {
		return UpdateType.BANKRUPTCY;
	}

}
