package com.strategists.game.update;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Activity.Type;

public class NewActivityUpdatePayload extends AbstractUpdatePayload<Activity> {

	public NewActivityUpdatePayload(Activity activity) {
		super(Type.NEW, activity);
	}

}
