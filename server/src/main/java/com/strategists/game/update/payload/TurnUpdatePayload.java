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
	private List<Player> payload;

	public TurnUpdatePayload(Activity activity, Player currentPlayer, Player previousPlayer) {
		this.activity = activity;
		this.payload = List.of(currentPlayer, previousPlayer);
	}

	@Override
	public UpdateType getType() {
		return UpdateType.TURN;
	}

}
