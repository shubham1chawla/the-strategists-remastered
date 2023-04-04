package com.strategists.game.update;

import com.strategists.game.entity.Activity.Type;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class KickPlayerUpdatePayload extends AbstractUpdatePayload<String> {

	public KickPlayerUpdatePayload(String username) {
		super(Type.KICK, username);
	}

}
