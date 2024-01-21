package com.strategists.game.controller;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.request.GoogleLoginRequest;
import com.strategists.game.response.AuthenticationResponse;
import com.strategists.game.service.AuthenticationService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

	@Autowired
	private AuthenticationService authenticationService;

	@PostMapping
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody GoogleLoginRequest request) {
		Assert.isTrue(EmailValidator.getInstance().isValid(request.getEmail()), "Email is not valid!");
		try {
			return ResponseEntity.ok(authenticationService.authenticate(request));
		} catch (Exception ex) {
			log.error("Authentication failed! Message: {}", ex.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

}
