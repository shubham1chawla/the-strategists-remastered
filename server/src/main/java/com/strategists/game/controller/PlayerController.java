package com.strategists.game.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.entity.Game;
import com.strategists.game.request.GoogleOAuthCredential;
import com.strategists.game.request.InvestmentRequest;
import com.strategists.game.request.KickPlayerRequest;
import com.strategists.game.response.EnterGameResponse;
import com.strategists.game.service.AdviceService;
import com.strategists.game.service.GameService;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api/games/{code}/players")
public class PlayerController {

	@Autowired
	private GameService gameService;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private LandService landService;

	@Autowired(required = false)
	private AdviceService adviceService;

	@PostMapping
	public ResponseEntity<EnterGameResponse> addPlayer(@PathVariable String code,
			@RequestBody GoogleOAuthCredential credential) {

		// Checking if game exists
		Game game = null;
		try {
			game = gameService.getGameByCode(code);
		} catch (Exception ex) {
			log.warn(ex.getMessage());
			return ResponseEntity.notFound().build();
		}

		// Adding player to the game
		try {
			Assert.state(game.isLobby(), "Players can't join active games!");
			val player = playerService.addPlayer(game, credential.getEmail(), credential.getName());
			return ResponseEntity.ok(EnterGameResponse.fromPlayer(player));
		} catch (Exception ex) {
			log.warn(ex.getMessage());
			return ResponseEntity.badRequest().build();
		}

	}

	@DeleteMapping
	public void kickPlayer(@PathVariable String code, @RequestBody KickPlayerRequest request) {
		val game = gameService.getGameByCode(code);
		Assert.state(game.isLobby(), "Can't kick players in active game!");
		playerService.kickPlayer(request.getPlayerId());
	}

	@PostMapping("/{playerId}/lands")
	public void invest(@PathVariable String code, @PathVariable long playerId, @RequestBody InvestmentRequest request) {
		val game = gameService.getGameByCode(code);
		Assert.state(game.isActive(), "You need an active game to buy land!");

		val player = playerService.getCurrentPlayer(game);
		Assert.state(Objects.equals(playerId, player.getId()), "Requesting player is not the current player!");

		val land = landService.getLandByIndex(game, player.getIndex());
		Assert.state(Objects.equals(land.getId(), request.getLandId()), "Current player is not at the requested land!");

		playerService.invest(player, land, request.getOwnership());
	}

	@PatchMapping("/{playerId}/advices")
	public void markAdvicesViewed(@PathVariable String code, @PathVariable long playerId) {
		Assert.notNull(adviceService, "Advice Service is not enabled!");

		val player = playerService.getPlayerById(playerId);
		Assert.state(Objects.equals(code, player.getGameCode()), "Requesting player not in the game!");

		adviceService.markPlayerAdvicesViewed(player);
	}

}
