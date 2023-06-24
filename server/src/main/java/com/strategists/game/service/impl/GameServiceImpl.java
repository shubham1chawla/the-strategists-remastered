package com.strategists.game.service.impl;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.aop.ActivityMapping;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.service.GameService;
import com.strategists.game.service.PlayerService;

@Service
public class GameServiceImpl implements GameService {

	private static final Random RANDOM = new Random();

	@Value("${strategists.game.dice-size}")
	private Integer diceSize;

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
	public void start() {
		Assert.isTrue(playerService.getCount() > 0, "No players added!");
		playerService.assignTurn();
	}

	@Override
	public void next() {

		// Assigning turn to next player
		playerService.nextPlayer();

		// Moving the current player to a new position
		playerService.movePlayer(RANDOM.nextInt(diceSize) + 1);

	}

}
