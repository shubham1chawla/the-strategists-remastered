package com.strategists.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.service.GameService;

@RestController
@RequestMapping("/api/game")
public class GameController {

	@Autowired
	private GameService gameService;

	@PutMapping("/start")
	public void start() {
		Assert.state(gameService.isLobbyState(), "Game already started!");
		gameService.start();
	}

}
