package com.strategists.game.update;

import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Player;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class JoinPlayerUpdatePayload extends AbstractUpdatePayload<Player> {

	public JoinPlayerUpdatePayload(Player player) {
		super(Type.JOIN, player);
	}

}
