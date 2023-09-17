package com.strategists.game.activity.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class JoinUpdatePayload extends AbstractUpdatePayload<Player> {

	public JoinUpdatePayload(Activity activity, Player player) {
		super(activity, player);
	}

}
