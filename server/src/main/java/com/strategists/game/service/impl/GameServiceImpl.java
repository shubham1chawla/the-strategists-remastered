package com.strategists.game.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.strategists.game.service.GameService;
import com.strategists.game.service.PlayerService;

@Service
public class GameServiceImpl implements GameService {

	private enum State {
		ACTIVE, LOBBY
	}

	@Autowired
	private PlayerService playerService;

	private State state = State.LOBBY;

	@Override
	public boolean isActiveState() {
		return state == State.ACTIVE;
	}

	@Override
	public boolean isLobbyState() {
		return state == State.LOBBY;
	}

	@Override
	public void start() {

		// Setting game's state to ACTIVE
		state = State.ACTIVE;

		// Assigning turn to a player
		playerService.assignTurn();

	}

}
