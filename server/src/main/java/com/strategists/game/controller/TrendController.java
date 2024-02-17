package com.strategists.game.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.entity.Trend;
import com.strategists.game.repository.TrendRepository;
import com.strategists.game.service.GameService;

import lombok.val;

@RestController
@RequestMapping("/api/games/{gameId}/trends")
public class TrendController {

	@Autowired
	private GameService gameService;

	@Autowired
	private TrendRepository trendRepository;

	@GetMapping
	public List<Trend> getTrends(@PathVariable long gameId) {
		val game = gameService.getGameById(gameId);
		return trendRepository.findByGameOrderByIdAsc(game);
	}

}
