package com.strategists.game.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.aop.ActivityMapping;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.Player.State;
import com.strategists.game.entity.Rent;
import com.strategists.game.repository.PlayerRepository;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class PlayerServiceImpl implements PlayerService {

	private static final Random RANDOM = new Random();

	@PersistenceContext
	private EntityManager em;

	@Value("${strategists.game.password-length}")
	private Integer passwordLength;

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private LandService landService;

	@Override
	public long getCount() {
		return playerRepository.count();
	}

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
	public Player getPlayerByUsername(String username) {
		val opt = playerRepository.findByUsername(username);
		Assert.isTrue(opt.isPresent(), "No player found with username: " + username);

		log.info("Found player: {}", opt.get());
		return opt.get();
	}

	@Override
	@ActivityMapping(Type.JOIN)
	public Player addPlayer(String username, double cash) {
		log.info("Checking if {} username exists...", username);
		Assert.isTrue(!playerRepository.existsByUsername(username), username + " username already exists!");

		// generating password
		val password = IntStream.range(0, passwordLength).map(i -> 10).map(RANDOM::nextInt).mapToObj(Integer::toString)
				.collect(Collectors.joining());

		log.info("Creating player with {} username", username);
		return playerRepository.save(new Player(username, cash, password));
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
	public boolean isTurnAssigned() {
		return playerRepository.existsByTurn(true);
	}

	@Override
	public Player assignTurn() {
		Assert.state(!isTurnAssigned(), "Turn already assigned!");

		log.info("Randomly assigning turn to a player...");
		val players = getPlayers();
		val player = players.get(RANDOM.nextInt(players.size()));
		player.setTurn(true);
		playerRepository.save(player);

		log.info("Assigned turn to {}.", player.getUsername());
		return player;
	}

	@Override
	public Player getCurrentPlayer() {
		val opt = playerRepository.findByTurn(true);
		Assert.state(opt.isPresent(), "No player has the turn!");

		return opt.get();
	}

	@Override
	@ActivityMapping(Type.MOVE)
	public Land movePlayer(Player player, int move) {
		player.setIndex((player.getIndex() + move) % landService.getCount());
		playerRepository.save(player);

		log.info("Moved {} to index: {}", player.getUsername(), player.getIndex());
		return landService.getLandByIndex(player.getIndex());
	}

	@Override
	@ActivityMapping(Type.TURN)
	public Player nextPlayer(Player currentPlayer) {
		Assert.state(currentPlayer.isTurn(),
				currentPlayer.getUsername() + " should have the turn to find who's the next player!");

		log.info("Finding next player of {}", currentPlayer.getUsername());

		val players = playerRepository.findAll();
		int i = players.indexOf(currentPlayer);

		// Finding suitable player
		do {
			i = (i + 1) % players.size();
			val player = players.get(i);

			// Checking if next player is not bankrupt and not current player
			if (player.isBankrupt() || Objects.equals(currentPlayer.getId(), player.getId())) {
				continue;
			}

			currentPlayer.setTurn(false);
			player.setTurn(true);
			playerRepository.saveAll(List.of(currentPlayer, player));

			log.info("Assigned turn to {}.", player.getUsername());
			return player;

		} while (!Objects.equals(currentPlayer.getId(), players.get(i).getId()));

		log.warn("No suitable next player found!");
		return null;
	}

	@Override
	@Transactional
	@ActivityMapping(Type.INVEST)
	public void invest(Player player, Land land, double ownership) {
		val buyAmount = land.getMarketValue() * (ownership / 100);
		Assert.isTrue(land.getTotalOwnership() + ownership <= 100, "Can't buy more than 100% of a land!");
		Assert.isTrue(player.getCash() > buyAmount, "You don't have enough cash to buy this land!");

		player.addLand(land, ownership, buyAmount);

		/*
		 * Somehow standard "save" method was not inserting the record right away. My
		 * guess, since PlayerLand has complex Id situation, JPA is unable to predict
		 * that it needs to insert/update the record. This behavior breaks refreshing of
		 * land's entity. The "saveAndFlush" method forces JPA to insert/update the
		 * record right away.
		 */
		playerRepository.saveAndFlush(player);

		// Refreshing land's entity to reflect this investment
		em.refresh(land);

		log.info("Player {} invested in {}.", player.getUsername(), land.getName());
	}

	@Override
	@Transactional
	@ActivityMapping(Type.RENT)
	public void payRent(Rent rent) {
		val source = rent.getSourcePlayer();
		val target = rent.getTargetPlayer();
		val land = rent.getLand();
		val amount = rent.getRentAmount();

		/*
		 * Adding rent instance to target player only to ensure that there is only rent
		 * entry in the database.
		 */
		target.addRent(rent);
		playerRepository.save(target);

		// Refreshing source player's entity to reflect correct cash
		em.refresh(source);

		log.info("{} paid {} rent to {} for {}", source.getUsername(), amount, target.getUsername(), land.getName());
	}

	@Override
	@ActivityMapping(Type.BANKRUPT)
	public void bankruptPlayer(Player player) {
		player.setState(State.BANKRUPT);
		playerRepository.save(player);

		log.info("Updated {}'s state to {}", player.getUsername(), player.getState());
	}

}
