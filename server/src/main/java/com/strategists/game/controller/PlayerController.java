package com.strategists.game.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.entity.Player;
import com.strategists.game.service.GameService;
import com.strategists.game.service.PlayerService;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	private class Keys {
		private static final String USERNAME = "username";
		private static final String CASH = "cash";
		private static final String ID = "id";
		private static final String OWNERSHIP = "ownership";
	}

	@Autowired
	private GameService gameService;

	@Autowired
	private PlayerService playerService;

	@GetMapping
	public List<Player> getPlayers() {
		return playerService.getPlayers();
	}

	@PostMapping
	public Player addPlayer(@RequestBody Map<String, Object> body) {
		Assert.state(gameService.isLobbyState(), "Can't add players to active game!");
		return playerService.addPlayer((String) body.get(Keys.USERNAME), (double) body.get(Keys.CASH));
	}

	@DeleteMapping
	public void kickPlayer(@RequestBody Map<String, Object> map) {
		Assert.state(gameService.isLobbyState(), "Can't kick players in active game!");
		playerService.kickPlayer(Integer.toUnsignedLong((int) map.get(Keys.ID)));
	}

	@PostMapping("/{playerId}/lands")
	public void buyLand(@RequestBody Map<String, Object> map) {
		Assert.state(gameService.isActiveState(), "You need an active game to buy land!");
		playerService.buyLand((double) map.get(Keys.OWNERSHIP));
	}

}
