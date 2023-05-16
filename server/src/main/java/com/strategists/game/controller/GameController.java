package com.strategists.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.service.GameService;
import com.strategists.game.service.GameService.State;

@RestController
@RequestMapping("/api/game")
public class GameController {

	@Autowired
	private GameService gameService;

	@GetMapping
	public State getState() {
		return gameService.getState();
	}

	@PutMapping("/start")
	public void start() {
		Assert.state(gameService.isState(State.LOBBY), "Game already started!");
		gameService.start();
	}

}
