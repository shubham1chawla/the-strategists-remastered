package com.strategists.game.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.Player.State;
import com.strategists.game.repository.PlayerRepository;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class PlayerServiceImpl implements PlayerService {

	private static final Random RANDOM = new Random();

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private LandService landService;

	@Override
	public List<Player> getPlayers() {
		return playerRepository.findAll();
	}

	@Override
	public Player addPlayer(String username, double cash) {
		log.info("Checking if {} username exists...", username);
		Assert.isTrue(!playerRepository.existsByUsername(username), username + " username already exists!");

		log.info("Creating player with {} username", username);
		return playerRepository.save(new Player(username, cash));
	}

	@Override
	public void kickPlayer(long id) {
		try {
			playerRepository.deleteById(id);
			log.info("Kicked player with ID: {}", id);
		} catch (EmptyResultDataAccessException ex) {
			// suppress exception
		}
	}

	@Override
	public void assignTurn() {
		Assert.state(!playerRepository.existsByTurn(true), "Turn already assigned!");

		log.info("Randomly assigning turn to a player...");
		final List<Player> players = getPlayers();
		final Player player = players.get(RANDOM.nextInt(players.size()));
		player.setTurn(true);
		playerRepository.save(player);

		log.info("Assigned turn to {}.", player.getUsername());
	}

	@Override
	public Player getCurrentPlayer() {
		final Optional<Player> opt = playerRepository.findByTurn(true);
		Assert.state(opt.isPresent(), "No player has the turn!");

		log.info("{} has the turn.", opt.get().getUsername());
		return opt.get();
	}

	@Override
	public void movePlayer(int move) {
		final int count = landService.getCount();

		final Player player = getCurrentPlayer();
		player.setIndex(player.getIndex() + move < count ? player.getIndex() + move : player.getIndex() + move - count);
		playerRepository.save(player);

		log.info("Moved {} to index: {}", player.getUsername(), player.getIndex());
	}

	@Override
	public void nextPlayer() {
		Assert.state(playerRepository.existsByTurn(true), "No player has the turn!");

		log.info("Assigning turn to the next player...");
		final List<Player> players = playerRepository.findByStateIn(Set.of(State.ACTIVE, State.JAIL));
		int index = -1;
		for (int i = 0; i < players.size(); i++) {
			final Player player = players.get(i);
			if (player.isTurn()) {
				player.setTurn(false);
				playerRepository.save(player);
				index = i;
				break;
			}
		}

		final Player player = players.get(index + 1 < players.size() ? index + 1 : 0);
		player.setTurn(true);
		playerRepository.save(player);

		log.info("Assigned turn to {}.", player.getUsername());
	}

	@Override
	public void buyLand(double ownership) {
		final Player player = getCurrentPlayer();
		final Land land = landService.getLandByIndex(player.getIndex());
		final double buyAmount = land.getMarketValue() * (ownership / 100);

		Assert.isTrue(land.getTotalOwnership() + ownership <= 100, "Can't buy more than 100% of a land!");
		Assert.isTrue(player.getCash() > buyAmount, "You don't have enough cash to buy this land!");

		player.addLand(land, ownership, buyAmount);
		playerRepository.save(player);

		log.info("Player {} invested in {}.", player.getUsername(), land.getName());
	}

}
