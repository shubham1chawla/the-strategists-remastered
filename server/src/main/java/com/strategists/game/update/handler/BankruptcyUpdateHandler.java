package com.strategists.game.update.handler;

import java.util.HashSet;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.BankruptcyUpdatePayload;

import lombok.val;

@Component
public class BankruptcyUpdateHandler extends AbstractUpdateHandler<BankruptcyUpdatePayload> {

	@Override
	public UpdateType getType() {
		return UpdateType.BANKRUPTCY;
	}

	@Override
	public void handle(Object returnValue, Object[] args) {
		// Bankrupt player as per the argument
		val player = (Player) args[0];

		// Extracting all the impacted players and lands
		val players = new HashSet<Player>();
		players.add(player);
		val lands = player.getPlayerLands().stream().map(PlayerLand::getLand).toList();
		for (Land land : lands) {
			players.addAll(land.getPlayerLands().stream().map(PlayerLand::getPlayer).toList());
		}

		// Persisting the activity and sending the update
		val activity = Activity.ofBankruptcy(player);
		sendUpdate(player.getGame(), new BankruptcyUpdatePayload(saveActivity(activity), lands, players));
	}

}
