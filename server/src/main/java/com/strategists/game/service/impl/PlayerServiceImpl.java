package com.strategists.game.service.impl;

import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.aop.ActivityMapping;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.Player.State;
import com.strategists.game.repository.PlayerRepository;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;

import lombok.val;
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
	public Player getPlayerById(long id) {
		val opt = playerRepository.findById(id);
		Assert.isTrue(opt.isPresent(), "No player found with ID: " + id);

		log.info("Found player: {}", opt.get());
		return opt.get();
	}

	@Override
	@ActivityMapping(Type.JOIN)
	public Player addPlayer(String username, double cash) {
		log.info("Checking if {} username exists...", username);
		Assert.isTrue(!playerRepository.existsByUsername(username), username + " username already exists!");

		log.info("Creating player with {} username", username);
		return playerRepository.save(new Player(username, cash));
	}

	@Override
	@Transactional
	@ActivityMapping(Type.KICK)
	public void kickPlayer(String username) {
		try {
			playerRepository.deleteByUsername(username);
			log.info("Kicked {}", username);
		} catch (EmptyResultDataAccessException ex) {
			// suppress exception
		}
	}

	@Override
	public void assignTurn() {
		Assert.state(!playerRepository.existsByTurn(true), "Turn already assigned!");

		log.info("Randomly assigning turn to a player...");
		val players = getPlayers();
		val player = players.get(RANDOM.nextInt(players.size()));
		player.setTurn(true);
		playerRepository.save(player);

		log.info("Assigned turn to {}.", player.getUsername());
	}

	@Override
	public Player getCurrentPlayer() {
		val opt = playerRepository.findByTurn(true);
		Assert.state(opt.isPresent(), "No player has the turn!");

		return opt.get();
	}

	@Override
	public void movePlayer(int move) {
		val count = landService.getCount();

		val player = getCurrentPlayer();
		player.setIndex(player.getIndex() + move < count ? player.getIndex() + move : player.getIndex() + move - count);
		playerRepository.save(player);

		log.info("Moved {} to index: {}", player.getUsername(), player.getIndex());
	}

	@Override
	public void nextPlayer() {
		Assert.state(playerRepository.existsByTurn(true), "No player has the turn!");

		log.info("Assigning turn to the next player...");
		val players = playerRepository.findByStateIn(Set.of(State.ACTIVE, State.JAIL));
		int index = -1;
		for (int i = 0; i < players.size(); i++) {
			val player = players.get(i);
			if (player.isTurn()) {
				player.setTurn(false);
				playerRepository.save(player);
				index = i;
				break;
			}
		}

		val player = players.get(index + 1 < players.size() ? index + 1 : 0);
		player.setTurn(true);
		playerRepository.save(player);

		log.info("Assigned turn to {}.", player.getUsername());
	}

	@Override
	@ActivityMapping(Type.BUY)
	public void buyLand(double ownership) {
		val player = getCurrentPlayer();
		val land = landService.getLandByIndex(player.getIndex());
		val buyAmount = land.getMarketValue() * (ownership / 100);

		Assert.isTrue(land.getTotalOwnership() + ownership <= 100, "Can't buy more than 100% of a land!");
		Assert.isTrue(player.getCash() > buyAmount, "You don't have enough cash to buy this land!");

		player.addLand(land, ownership, buyAmount);
		playerRepository.save(player);

		log.info("Player {} invested in {}.", player.getUsername(), land.getName());
	}

}
