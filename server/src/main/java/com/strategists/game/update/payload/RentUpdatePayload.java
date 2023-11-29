package com.strategists.game.update.payload;

import java.util.List;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RentUpdatePayload implements UpdatePayload<List<Player>> {

	private Activity activity;
	private Player source;
	private Player target;

	@Override
	public UpdateType getType() {
		return UpdateType.RENT;
	}

	@Override
	public List<Player> getPayload() {
		return List.of(source, target);
	}

}
