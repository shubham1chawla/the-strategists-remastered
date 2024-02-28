package com.strategists.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.strategists.game.service.GameService;
import com.strategists.game.service.UpdateService;

import lombok.val;

@RestController
@RequestMapping("/api/games/{code}/sse")
public class UpdateController {

	@Autowired
	private GameService gameService;

	@Autowired
	private UpdateService updateService;

	@GetMapping
	public SseEmitter getSseEmitter(@PathVariable String code, @RequestParam(required = true) String username) {
		val game = gameService.getGameByCode(code);
		return updateService.registerEmitter(game, username);
	}

}
