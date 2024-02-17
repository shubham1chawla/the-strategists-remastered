package com.strategists.game.service.impl;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Game.State;
import com.strategists.game.entity.GameMap;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.entity.Rent;
import com.strategists.game.repository.GameRepository;
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
	private GameRepository gameRepository;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private LandService landService;

	@Override
	public Game createGame(String adminUsername, String adminEmail, GameMap gameMap) {
		Assert.state(!gameRepository.existsByAdminEmail(adminEmail), adminUsername + " already created a game!");

		val game = gameRepository.save(new Game(adminUsername, adminEmail));
		gameMap.getLands().forEach(land -> land.setGame(game));
		landService.save(gameMap.getLands());

		log.info("Created game ID {} for admin: {}", game.getId(), adminUsername);
		return game;
	}

	@Override
	public Game getGameByAdminEmail(String adminEmail) {
		val opt = gameRepository.findByAdminEmail(adminEmail);
		Assert.isTrue(opt.isPresent(), "No game found for admin: " + adminEmail);
		return opt.get();
	}

	@Override
	public Game getGameById(long id) {
		val opt = gameRepository.findById(id);
		Assert.isTrue(opt.isPresent(), "No game found for ID: " + id);
		return opt.get();
	}

	@Override
	public void startGame(Game game) {
		// Changing game's state
		game.setState(State.ACTIVE);
		game = gameRepository.save(game);

		// Assigning turn
		playerService.assignTurn(game);

		// Updating initial trends
		updateTrends(game);
	}

	@Override
	@UpdateMapping(UpdateType.END)
	public Player playTurn(Game game) {

		// Checking if game has ended
		val winner = getWinnerPlayer(game);
		if (winner.isPresent()) {
			return winner.get();
		}

		// Assigning turn to next player
		val player = playerService.nextPlayer(playerService.getCurrentPlayer(game));

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

		// Updating trends
		updateTrends(game);

		// No winner declared
		return null;
	}

	@Override
	@UpdateMapping(UpdateType.RESET)
	public void resetGame(Game game) {
		// Changing game's state
		game.setState(State.LOBBY);
		game = gameRepository.save(game);

		// Resetting players
		playerService.resetPlayers(game);

		// Reseting lands
		landService.resetLands(game);
	}

	private Optional<Player> getWinnerPlayer(Game game) {
		val activePlayers = playerService.getActivePlayersByGame(game);
		if (activePlayers.size() > 1) {
			return Optional.empty();
		}

		val winner = activePlayers.get(0);
		log.info("Found winner {} for game ID: {}", winner.getUsername(), game.getId());
		return Optional.of(winner);
	}

	private void updateTrends(Game game) {
		playerService.updatePlayerTrends(game);
		landService.updateLandTrends(game);
	}

}
