package com.strategists.game.service;

public interface AuthService {

	enum Type {
		ADMIN, PLAYER;
	}

	Type authenticate(String username, String password);

}
