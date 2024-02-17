package com.strategists.game.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.entity.Land;
import com.strategists.game.service.GameService;
import com.strategists.game.service.LandService;

import lombok.val;

@RestController
@RequestMapping("/api/games/{gameId}/lands")
public class LandController {

	@Autowired
	private GameService gameService;

	@Autowired
	private LandService landService;

	@GetMapping
	public List<Land> getLands(@PathVariable long gameId) {
		val game = gameService.getGameById(gameId);
		return landService.getLandsByGame(game);
	}

}
