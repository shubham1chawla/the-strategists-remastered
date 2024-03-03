package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.update.UpdateType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateUpdatePayload implements UpdatePayload<Object> {

	private Activity activity;

	@Override
	public Object getPayload() {
		return null;
	}

	@Override
	public UpdateType getType() {
		return UpdateType.CREATE;
	}

}
