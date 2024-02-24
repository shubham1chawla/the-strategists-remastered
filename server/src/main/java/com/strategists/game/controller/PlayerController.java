package com.strategists.game.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.entity.Player;
import com.strategists.game.request.InvestmentRequest;
import com.strategists.game.request.InvitePlayerRequest;
import com.strategists.game.request.KickPlayerRequest;
import com.strategists.game.service.GameService;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;

import lombok.val;

@RestController
@RequestMapping("/api/games/{gameId}/players")
public class PlayerController {

	@Autowired
	private GameService gameService;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private LandService landService;

	@PostMapping
	public Player sendInvite(@PathVariable long gameId, @RequestBody InvitePlayerRequest request) {
		val game = gameService.getGameById(gameId);
		Assert.state(game.isLobby(), "Can't add players to active game!");
		return playerService.sendInvite(game, request.getEmail(), request.getCash());
	}

	@DeleteMapping
	public void kickPlayer(@PathVariable long gameId, @RequestBody KickPlayerRequest request) {
		val game = gameService.getGameById(gameId);
		Assert.state(game.isLobby(), "Can't kick players in active game!");
		playerService.kickPlayer(request.getPlayerId());
	}

	@PostMapping("/{playerId}/lands")
	public void invest(@PathVariable long gameId, @PathVariable long playerId, @RequestBody InvestmentRequest request) {
		val game = gameService.getGameById(gameId);
		Assert.state(game.isActive(), "You need an active game to buy land!");

		val player = playerService.getCurrentPlayer(game);
		Assert.state(Objects.equals(playerId, player.getId()), "Requesting player is not the current player!");

		val land = landService.getLandByIndex(game, player.getIndex());
		Assert.state(Objects.equals(land.getId(), request.getLandId()), "Current player is not at the requested land!");

		playerService.invest(player, land, request.getOwnership());
	}

}
