package com.strategists.game.service.impl;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.aop.ActivityMapping;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.entity.Rent;
import com.strategists.game.service.GameService;
import com.strategists.game.service.PlayerService;

import lombok.val;

@Service
public class GameServiceImpl implements GameService {

	private static final Random RANDOM = new Random();

	@Value("${strategists.game.dice-size}")
	private Integer diceSize;

	@Value("${strategists.game.rent-factor}")
	private Double rentFactor;

	@Autowired
	private PlayerService playerService;

	@Override
	public State getState() {
		return playerService.isTurnAssigned() ? State.ACTIVE : State.LOBBY;
	}

	@Override
	public boolean isState(State state) {
		return getState().equals(state);
	}

	@Override
	@ActivityMapping(Type.START)
	public Player start() {
		Assert.isTrue(playerService.getCount() > 0, "No players added!");
		return playerService.assignTurn();
	}

	@Override
	public void next() {

		// Assigning turn to next player
		val player = playerService.nextPlayer(playerService.getCurrentPlayer());
		if (Objects.isNull(player)) {
			return;
		}

		// Moving the current player to a new position
		val land = playerService.movePlayer(player, RANDOM.nextInt(diceSize) + 1);

		// Paying rent to players on current land
		for (PlayerLand pl : new ArrayList<>(land.getPlayerLands())) {
			val targetPlayer = pl.getPlayer();

			// Avoiding self rent payment and bankrupt players
			if (Objects.equals(targetPlayer.getId(), player.getId()) || targetPlayer.isBankrupt()) {
				continue;
			}

			// Paying rent to target player
			val rentAmount = rentFactor * (pl.getOwnership() / 100) * land.getMarketValue();
			playerService.payRent(new Rent(player, targetPlayer, land, rentAmount));
		}

		// Checking if player is bankrupt
		if (player.getCash() < 0) {
			playerService.bankruptPlayer(player);
		}

	}

}
