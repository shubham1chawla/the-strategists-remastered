package com.strategists.game.response;

import com.strategists.game.service.AuthenticationService.Type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationResponse {

	private long gameId;
	private String username;
	private Type type;

}
