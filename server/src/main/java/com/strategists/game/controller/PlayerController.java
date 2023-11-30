package com.strategists.game.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.entity.Player;
import com.strategists.game.request.AddPlayerRequest;
import com.strategists.game.request.InvestmentRequest;
import com.strategists.game.request.KickPlayerRequest;
import com.strategists.game.service.GameService;
import com.strategists.game.service.GameService.State;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;

import lombok.val;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

	@Autowired
	private GameService gameService;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private LandService landService;

	@GetMapping
	public List<Player> getPlayers() {
		return playerService.getPlayers();
	}

	@GetMapping("/{playerId}/password")
	public String getPassword(@PathVariable Long playerId) {
		return playerService.getPlayerById(playerId).getPassword();
	}

	@PostMapping
	public Player addPlayer(@RequestBody AddPlayerRequest request) {
		Assert.state(gameService.isState(State.LOBBY), "Can't add players to active game!");
		return playerService.addPlayer(request.getUsername(), request.getCash());
	}

	@DeleteMapping
	public void kickPlayer(@RequestBody KickPlayerRequest request) {
		Assert.state(gameService.isState(State.LOBBY), "Can't kick players in active game!");
		playerService.kickPlayer(request.getUsername());
	}

	@PostMapping("/{playerId}/lands")
	public void invest(@PathVariable Long playerId, @RequestBody InvestmentRequest request) {
		Assert.state(gameService.isState(State.ACTIVE), "You need an active game to buy land!");

		val player = playerService.getCurrentPlayer();
		Assert.state(Objects.equals(playerId, player.getId()), "Requesting player is not the current player!");

		val land = landService.getLandByIndex(player.getIndex());
		Assert.state(Objects.equals(land.getId(), request.getLandId()), "Current player is not at the requested land!");

		playerService.invest(player, land, request.getOwnership());
	}

}
