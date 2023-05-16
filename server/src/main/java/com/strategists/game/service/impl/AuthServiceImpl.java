package com.strategists.game.service.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.service.AuthService;
import com.strategists.game.service.PlayerService;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AuthServiceImpl implements AuthService {

	@Value("${strategists.admin.username}")
	private String adminUsername;

	@Value("${strategists.admin.password}")
	private String adminPassword;

	@Autowired
	private PlayerService playerService;

	@Override
	public Type authenticate(String username, String password) {

		// Checking if player is admin
		if (Objects.equals(adminUsername, username) && Objects.equals(adminPassword, password)) {
			log.info("Authenticating admin: {}", username);
			return Type.ADMIN;
		}

		// Checking if its a standard player
		val player = playerService.getPlayerByUsername(username);
		Assert.isTrue(Objects.equals(player.getPassword(), password), "Incorrect credentials!");

		log.info("Authenticating player: {}", username);
		return Type.PLAYER;
	}

}
