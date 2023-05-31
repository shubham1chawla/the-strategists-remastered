package com.strategists.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.request.AuthPlayerRequest;
import com.strategists.game.service.AuthService;
import com.strategists.game.service.AuthService.Type;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping
	public ResponseEntity<Type> authenticate(@RequestBody AuthPlayerRequest request) {
		Assert.hasText(request.getUsername(), "Username shouldn't be empty!");
		Assert.hasText(request.getPassword(), "Password shouldn't be empty!");
		try {
			return ResponseEntity.ok(authService.authenticate(request.getUsername(), request.getPassword()));
		} catch (Exception ex) {
			log.error("Authentication attempt failed! Message: {}", ex.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

}
