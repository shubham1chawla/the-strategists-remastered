package com.strategists.game.response;

import com.strategists.game.service.AuthenticationService.Type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationResponse {

	String username;
	Type type;

}
