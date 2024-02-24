package com.strategists.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.repository.TrendRepository;
import com.strategists.game.response.GameResponse;
import com.strategists.game.service.GameService;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/games/{gameId}")
public class GameController {

	@Autowired
	private GameService gameService;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private LandService landService;

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private TrendRepository trendRepository;

	@GetMapping
	public ResponseEntity<GameResponse> getGameResponse(@PathVariable long gameId) {
		try {

			// Finding requested game
			val game = gameService.getGameById(gameId);

			// Creating response for the game
			val response = GameResponse.builder().game(game).players(playerService.getPlayersByGame(game))
					.lands(landService.getLandsByGame(game))
					.activities(activityRepository.findByGameOrderByIdDesc(game))
					.trends(trendRepository.findByGameOrderByIdAsc(game)).build();

			// Responding with 200
			return ResponseEntity.ok(response);

		} catch (Exception ex) {
			log.warn(ex.getMessage());
			return ResponseEntity.notFound().build();
		}
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
