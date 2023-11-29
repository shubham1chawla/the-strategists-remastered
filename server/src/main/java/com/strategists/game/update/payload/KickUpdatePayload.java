package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.update.UpdateType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KickUpdatePayload implements UpdatePayload<String> {

	private Activity activity;
	private String payload;

	@Override
	public UpdateType getType() {
		return UpdateType.KICK;
	}

}
