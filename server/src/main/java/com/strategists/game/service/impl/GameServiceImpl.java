package com.strategists.game.service.impl;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.activity.ActivityMapping;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.entity.Rent;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.service.GameService;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class GameServiceImpl implements GameService {

	private static final Random RANDOM = new Random();

	@Value("${strategists.game.dice-size}")
	private Integer diceSize;

	@Value("${strategists.game.rent-factor}")
	private Double rentFactor;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private LandService landService;

	@Autowired
	private ActivityRepository activityRepository;

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
	@ActivityMapping(Type.END)
	public Player next() {

		// Current player will be the previous player now
		val prevPlayer = playerService.getCurrentPlayer();

		// Assigning turn to next player
		val player = playerService.nextPlayer(prevPlayer);
		if (Objects.isNull(player)) {
			log.info("Player {} is the winner", prevPlayer.getUsername());
			return prevPlayer;
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

		// No winner declared
		return null;
	}

	@Override
	@ActivityMapping(Type.RESET)
	public void reset() {
		// Resetting players
		playerService.resetPlayers();

		// Reseting lands
		landService.resetLands();

		// Reseting activities
		activityRepository.deleteAll();
	}

}
