package com.strategists.game.service;

public interface GameService {

	enum State {
		LOBBY, ACTIVE;
	}

	State getState();

	boolean isState(State state);

	void start();

	void next();

}
