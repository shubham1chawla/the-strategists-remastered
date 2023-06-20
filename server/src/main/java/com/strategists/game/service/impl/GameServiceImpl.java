package com.strategists.game.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.aop.ActivityMapping;
import com.strategists.game.aop.UpdateMapping;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.service.GameService;
import com.strategists.game.service.PlayerService;

@Service
public class GameServiceImpl implements GameService {

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
	@UpdateMapping(Type.START)
	@ActivityMapping(Type.START)
	public void start() {
		Assert.isTrue(playerService.getCount() > 0, "No players added!");
		playerService.assignTurn();
	}

	@Override
	public void next() {

		// Assigning turn to next player
		playerService.nextPlayer();

	}

}
