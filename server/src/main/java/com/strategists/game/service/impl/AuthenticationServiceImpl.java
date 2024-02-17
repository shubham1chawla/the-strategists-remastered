package com.strategists.game.service.impl;

import java.io.File;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.GameMap;
import com.strategists.game.request.GoogleLoginRequest;
import com.strategists.game.request.GoogleRecaptchaVerificationRequest;
import com.strategists.game.response.AuthenticationResponse;
import com.strategists.game.response.GoogleRecaptchaVerificationResponse;
import com.strategists.game.service.AuthenticationService;
import com.strategists.game.service.GameService;
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

	@Value("${strategists.game.default-map}")
	private String defaultGameMap;

	@Value("${strategists.configuration.google-recaptcha-api-url}")
	private String googleRecaptchaAPIUrl;

	@Value("${strategists.configuration.google-recaptcha-secret-key}")
	private String googleRecaptchaSecretKey;

	@Autowired
	private Map<String, File> gameMapFiles;

	@Autowired
	private GameService gameService;

	@Autowired
	private PlayerService playerService;

	@PostConstruct
	public void setup() {
		Assert.isTrue(EmailValidator.getInstance().isValid(adminEmail), "Setup a valid admin email id!");
		Assert.hasText(googleRecaptchaAPIUrl, "Google Recaptcha API URL is not provided!");
		Assert.hasText(googleRecaptchaSecretKey, "Google Recaptcha Secret key is not provided!");
	}

	@Override
	public AuthenticationResponse authenticate(GoogleLoginRequest request) {

		// Checking if player is admin
		if (Objects.equals(request.getEmail(), adminEmail)) {

			// Checking if game exists for admin
			Game game = null;
			try {
				game = gameService.getGameByAdminEmail(adminEmail);
			} catch (Exception ex) {
				val gameMap = GameMap.from(gameMapFiles.get(defaultGameMap));
				game = gameService.createGame(adminUsername, adminEmail, gameMap);
			}

			log.info("Authenticating admin {} as {}", request.getName(), adminUsername);
			return new AuthenticationResponse(game.getId(), game.getAdminUsername(), Type.ADMIN);
		}

		// Checking if its a standard player
		val player = playerService.getPlayerByEmail(request.getEmail());
		if (player.isInvited()) {
			playerService.acceptInvite(request);
		}

		log.info("Authenticating player {} for game ID: {}", player.getUsername(), player.getGameId());
		return new AuthenticationResponse(player.getGameId(), player.getUsername(), Type.PLAYER);
	}

	@Override
	public boolean verify(GoogleRecaptchaVerificationRequest request) {
		val headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

		val body = new LinkedMultiValueMap<String, String>();
		body.add("secret", googleRecaptchaSecretKey);
		body.add("response", request.getClientToken());

		val entity = new HttpEntity<>(body, headers);
		val template = new RestTemplate();
		val response = template.postForEntity(googleRecaptchaAPIUrl, entity, GoogleRecaptchaVerificationResponse.class);
		return response.hasBody() && response.getBody().isSuccess();
	}

}
