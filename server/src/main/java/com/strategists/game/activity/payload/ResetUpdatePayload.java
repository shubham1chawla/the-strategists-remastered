package com.strategists.game.activity.payload;

import com.strategists.game.entity.Activity;

public class ResetUpdatePayload extends AbstractUpdatePayload<Object> {

	public ResetUpdatePayload(Activity activity) {
		super(activity, null);
	}

}
