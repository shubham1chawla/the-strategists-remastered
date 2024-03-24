package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.update.UpdateType;

public class CleanUpUpdatePayload implements UpdatePayload<Object> {

	@Override
	public UpdateType getType() {
		return UpdateType.CLEAN_UP;
	}

	@Override
	public Activity getActivity() {
		return null;
	}

	@Override
	public Object getPayload() {
		return null;
	}

}
