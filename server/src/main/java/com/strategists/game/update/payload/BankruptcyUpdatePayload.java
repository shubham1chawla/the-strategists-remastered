package com.strategists.game.update.payload;

import java.util.Collection;
import java.util.Map;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BankruptcyUpdatePayload implements UpdatePayload<Map<String, Object>> {

	private Activity activity;
	private Collection<Land> lands;
	private Collection<Player> players;

	@Override
	public UpdateType getType() {
		return UpdateType.BANKRUPTCY;
	}

	@Override
	public Map<String, Object> getPayload() {
		return Map.of("lands", lands, "players", players);
	}

}
