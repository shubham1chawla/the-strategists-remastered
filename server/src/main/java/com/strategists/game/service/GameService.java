package com.strategists.game.service;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface GameService {

	@Getter
	@AllArgsConstructor
	enum State {
		LOBBY("lobby"), ACTIVE("active");

		@JsonValue
		private String value;
	}

	State getState();

	boolean isState(State state);

	void start();

	void next();

}
