package com.strategists.game.activity;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.update.InvestUpdatePayload;

import lombok.val;

@Component
public class InvestActivityHandler implements ActivityHandler<InvestUpdatePayload> {

	@Override
	public Optional<InvestUpdatePayload> apply(Object obj, Object[] args) {
		val player = (Player) args[0];
		val land = (Land) args[1];
		val ownership = (double) args[2];

		/*
		 * Updating all the players that are linked with this land. Each player's
		 * net-worth is tied with the market value of the land, therefore investment in
		 * this land will boost each investors' net-worth.
		 */
		val players = land.getPlayerLands().stream().map(PlayerLand::getPlayer).toList();

		val activity = Activity.ofInvest(player.getUsername(), ownership, land.getName());
		return Optional.of(new InvestUpdatePayload(activity, land, players));
	}

	@Override
	public Type getType() {
		return Type.INVEST;
	}

}
