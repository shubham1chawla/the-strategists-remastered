package com.strategists.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.entity.Game.State;
import com.strategists.game.service.GameService;

import lombok.val;

@RestController
@RequestMapping("/api/games/{gameId}")
public class GameController {

	@Autowired
	private GameService gameService;

	@GetMapping("/state")
	public State getState(@PathVariable long gameId) {
		return gameService.getGameById(gameId).getState();
	}

	@PutMapping("/start")
	public void startGame(@PathVariable long gameId) {
		val game = gameService.getGameById(gameId);
		Assert.state(game.isLobby(), "Game already started!");
		gameService.startGame(game);
	}

	@PutMapping("/turn")
	public void playTurn(@PathVariable long gameId) {
		val game = gameService.getGameById(gameId);
		Assert.state(game.isActive(), "Game not started yet!");
		gameService.playTurn(game);
	}

	@DeleteMapping
	public void resetGame(@PathVariable long gameId) {
		val game = gameService.getGameById(gameId);
		gameService.resetGame(game);
	}

}
