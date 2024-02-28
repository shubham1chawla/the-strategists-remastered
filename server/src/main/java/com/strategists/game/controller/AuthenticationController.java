package com.strategists.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.request.GoogleRecaptchaVerificationRequest;
import com.strategists.game.service.AuthenticationService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api")
public class AuthenticationController {

	@Autowired
	private AuthenticationService authenticationService;

	@PostMapping("/recaptcha")
	public ResponseEntity<Void> verify(@RequestBody GoogleRecaptchaVerificationRequest request) {
		try {
			Assert.hasText(request.getClientToken(), "Client Token can't be empty");
			Assert.isTrue(authenticationService.verify(request), "User is not verified!");
			return ResponseEntity.ok().build();
		} catch (Exception ex) {
			log.warn("Recaptcha verification failed! Message: {}", ex.getMessage(), ex);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

}
