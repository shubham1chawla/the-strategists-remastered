package com.strategists.game.service;

import com.strategists.game.entity.Player;

public interface GameService {

	enum State {
		LOBBY, ACTIVE;
	}

	State getState();

	boolean isState(State state);

	Player start();

	Player next();

	void reset();

}
