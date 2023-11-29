package com.strategists.game.update.payload;

import java.util.List;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TurnUpdatePayload implements UpdatePayload<List<Player>> {

	private Activity activity;
	private Player currentPlayer;
	private Player previousPlayer;

	@Override
	public UpdateType getType() {
		return UpdateType.TURN;
	}

	@Override
	public List<Player> getPayload() {
		return List.of(currentPlayer, previousPlayer);
	}

}
