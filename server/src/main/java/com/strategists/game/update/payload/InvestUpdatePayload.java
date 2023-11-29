package com.strategists.game.update.payload;

import java.util.List;
import java.util.Map;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvestUpdatePayload implements UpdatePayload<Map<String, Object>> {

	private Activity activity;
	private Land land;
	private List<Player> players;

	@Override
	public UpdateType getType() {
		return UpdateType.INVEST;
	}

	@Override
	public Map<String, Object> getPayload() {
		return Map.of("land", land, "players", players);
	}

}
