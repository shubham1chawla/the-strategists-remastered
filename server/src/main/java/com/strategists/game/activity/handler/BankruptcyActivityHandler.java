package com.strategists.game.activity.handler;

import java.util.HashSet;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.strategists.game.activity.payload.BankruptcyUpdatePayload;
import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;

import lombok.val;

@Component
public class BankruptcyActivityHandler implements ActivityHandler<BankruptcyUpdatePayload> {

	@Override
	public Optional<BankruptcyUpdatePayload> apply(Object obj, Object[] args) {
		val player = (Player) args[0];

		val players = new HashSet<Player>();
		val lands = player.getPlayerLands().stream().map(PlayerLand::getLand).toList();
		for (Land land : lands) {
			players.addAll(land.getPlayerLands().stream().map(PlayerLand::getPlayer).toList());
		}

		val activity = Activity.ofBankruptcy(player.getUsername());
		return Optional.of(new BankruptcyUpdatePayload(activity, lands, players));
	}

	@Override
	public Type getType() {
		return Type.BANKRUPTCY;
	}

}
