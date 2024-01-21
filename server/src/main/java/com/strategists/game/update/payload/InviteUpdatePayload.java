package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InviteUpdatePayload implements UpdatePayload<Player> {

	private Activity activity;
	private Player payload;

	@Override
	public UpdateType getType() {
		return UpdateType.INVITE;
	}

}
