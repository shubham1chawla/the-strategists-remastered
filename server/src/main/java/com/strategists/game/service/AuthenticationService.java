package com.strategists.game.service;

import com.strategists.game.request.GoogleLoginRequest;
import com.strategists.game.response.AuthenticationResponse;

public interface AuthenticationService {

	enum Type {
		ADMIN, PLAYER;
	}

	AuthenticationResponse authenticate(GoogleLoginRequest request);

}
