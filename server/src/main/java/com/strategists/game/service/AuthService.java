package com.strategists.game.service;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface AuthService {

	@Getter
	@AllArgsConstructor
	enum Type {
		ADMIN("admin"), PLAYER("player");

		@JsonValue
		private String value;
	}

	Type authenticate(String username, String password);

}
