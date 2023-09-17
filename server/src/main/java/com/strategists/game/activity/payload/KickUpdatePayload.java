package com.strategists.game.activity.payload;

import com.strategists.game.entity.Activity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class KickUpdatePayload extends AbstractUpdatePayload<String> {

	public KickUpdatePayload(Activity activity, String username) {
		super(activity, username);
	}

}
