package com.strategists.game.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.repository.TrendRepository;
import com.strategists.game.request.GoogleOAuthCredential;
import com.strategists.game.response.EnterGameResponse;
import com.strategists.game.response.GameResponse;
import com.strategists.game.service.GameService;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/games")
public class GameController {

	@Value("${strategists.security.allowed-emails}")
	private Set<String> allowedEmails;

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

	@GetMapping("/{code}")
	public ResponseEntity<GameResponse> getGameResponse(@PathVariable String code) {
		try {

			// Finding requested game
			val game = gameService.getGameByCode(code);

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

	@GetMapping
	public ResponseEntity<EnterGameResponse> findGame(@RequestParam(name = "credential", required = true) String jwt) {
		try {

			// Converting JWT string to credential instance
			val credential = GoogleOAuthCredential.fromJWT(jwt);

			// Finding player and associated game information
			val player = playerService.getPlayerByEmail(credential.getEmail());
			return ResponseEntity.ok(EnterGameResponse.fromPlayer(player));

		} catch (Exception ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	public ResponseEntity<EnterGameResponse> createGame(@RequestBody GoogleOAuthCredential credential) {

		// Checking if requesting user can create the game
		if (allowedEmails.contains(credential.getEmail())) {
			val player = gameService.createGame(credential);
			return ResponseEntity.ok(EnterGameResponse.fromPlayer(player));
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

	}

	@PutMapping("/{code}/start")
	public void startGame(@PathVariable String code) {
		val game = gameService.getGameByCode(code);
		Assert.state(game.isLobby(), "Game already started!");
		gameService.startGame(game);
	}

	@PutMapping("/{code}/turn")
	public void playTurn(@PathVariable String code) {
		val game = gameService.getGameByCode(code);
		Assert.state(game.isActive(), "Game not started yet!");
		gameService.playTurn(game);
	}

	@DeleteMapping("/{code}")
	public void resetGame(@PathVariable String code) {
		val game = gameService.getGameByCode(code);
		gameService.resetGame(game);
	}

}
