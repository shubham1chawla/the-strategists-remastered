package com.strategists.game.service.impl;

import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.request.GoogleLoginRequest;
import com.strategists.game.response.AuthenticationResponse;
import com.strategists.game.service.AuthenticationService;
import com.strategists.game.service.PlayerService;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	@Value("${strategists.admin.username}")
	private String adminUsername;

	@Value("${strategists.admin.email}")
	private String adminEmail;

	@Autowired
	private PlayerService playerService;

	@PostConstruct
	public void setup() {
		Assert.isTrue(EmailValidator.getInstance().isValid(adminEmail), "Setup a valid admin email id!");
	}

	@Override
	public AuthenticationResponse authenticate(GoogleLoginRequest request) {

		// Checking if player is admin
		if (Objects.equals(request.getEmail(), adminEmail)) {
			log.info("Authenticating admin {} as {}", request.getName(), adminUsername);
			return new AuthenticationResponse(adminUsername, Type.ADMIN);
		}

		// Checking if its a standard player
		val player = playerService.getPlayerByEmail(request.getEmail());
		if (player.isInvited()) {
			playerService.acceptInvite(request);
		}

		log.info("Authenticating player: {}", player.getUsername());
		return new AuthenticationResponse(player.getUsername(), Type.PLAYER);
	}

}
