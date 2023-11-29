package com.strategists.game.service.impl;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.entity.Rent;
import com.strategists.game.service.GameService;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;
import com.strategists.game.update.UpdateMapping;
import com.strategists.game.update.UpdateType;

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

	@Override
	public State getState() {
		return playerService.isTurnAssigned() ? State.ACTIVE : State.LOBBY;
	}

	@Override
	public boolean isState(State state) {
		return getState().equals(state);
	}

	@Override
	@UpdateMapping(UpdateType.START)
	public Player startGame() {
		Assert.isTrue(playerService.getCount() > 0, "No players added!");
		return playerService.assignTurn();
	}

	@Override
	@UpdateMapping(UpdateType.END)
	public Player playTurn() {

		// Checking if game has ended
		val winner = getWinnerPlayer();
		if (winner.isPresent()) {
			return winner.get();
		}

		// Assigning turn to next player
		val player = playerService.nextPlayer(playerService.getCurrentPlayer());

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
		if (player.getCash() <= 0) {
			playerService.bankruptPlayer(player);
		}

		// No winner declared
		return null;
	}

	private Optional<Player> getWinnerPlayer() {
		val activePlayers = playerService.getActivePlayers();
		if (activePlayers.size() > 1) {
			return Optional.empty();
		}

		val winner = activePlayers.get(0);
		log.info("Found winner: {}", winner.getUsername());
		return Optional.of(winner);
	}

	@Override
	@UpdateMapping(UpdateType.RESET)
	public void resetGame() {
		// Resetting players
		playerService.resetPlayers();

		// Reseting lands
		landService.resetLands();
	}

}
