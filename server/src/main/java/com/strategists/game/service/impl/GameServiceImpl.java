package com.strategists.game.service.impl;

import org.springframework.stereotype.Service;

import com.strategists.game.service.GameService;

@Service
public class GameServiceImpl implements GameService {

	private enum State {
		ACTIVE, LOBBY
	}

	private State state = State.LOBBY;

	@Override
	public boolean isActiveState() {
		return state == State.ACTIVE;
	}

	@Override
	public boolean isLobbyState() {
		return state == State.LOBBY;
	}

}
