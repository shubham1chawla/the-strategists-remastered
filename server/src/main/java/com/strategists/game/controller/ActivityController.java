package com.strategists.game.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.entity.Activity;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.service.GameService;

import lombok.val;

@RestController
@RequestMapping("/api/games/{gameId}/activities")
public class ActivityController {

	@Autowired
	private GameService gameService;

	@Autowired
	private ActivityRepository activityRepository;

	@GetMapping
	public List<Activity> getActivities(@PathVariable long gameId) {
		val game = gameService.getGameById(gameId);
		return activityRepository.findByGameOrderByIdDesc(game);
	}

}
