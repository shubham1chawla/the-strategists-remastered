package com.strategists.game.update.handler;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.InvestUpdatePayload;

import lombok.val;

@Component
public class InvestUpdateHandler extends AbstractUpdateHandler<InvestUpdatePayload> {

	@Override
	public UpdateType getType() {
		return UpdateType.INVEST;
	}

	@Override
	public void handle(Object returnValue, Object[] args) {
		// Investing player, invested land, and ownership amount from argument
		val player = (Player) args[0];
		val land = (Land) args[1];
		val ownership = (double) args[2];

		/*
		 * Updating all the players that are linked with this land. Each player's
		 * net-worth is tied with the market value of the land, therefore investment in
		 * this land will boost each investors' net-worth.
		 */
		val players = land.getPlayerLands().stream().map(PlayerLand::getPlayer).toList();

		// Persisting the activity and sending the update
		val activity = Activity.ofInvest(player.getUsername(), ownership, land.getName());
		sendUpdate(new InvestUpdatePayload(saveActivity(activity), land, players));
	}

}
