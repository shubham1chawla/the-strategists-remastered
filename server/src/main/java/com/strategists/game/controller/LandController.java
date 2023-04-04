package com.strategists.game.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.entity.Land;
import com.strategists.game.request.HostEventRequest;
import com.strategists.game.service.GameService;
import com.strategists.game.service.LandService;

@RestController
@RequestMapping("/api/lands")
public class LandController {

	@Autowired
	private GameService gameService;

	@Autowired
	private LandService landService;

	@GetMapping
	public List<Land> getLands() {
		return landService.getLands();
	}

	@PostMapping("/{landId}/events")
	public void hostEvent(@PathVariable long landId, @RequestBody HostEventRequest request) {
		Assert.isTrue(gameService.isActiveState(), "You need an active game to host events!");
		landService.hostEvent(landId, request.getEventId(), request.getLife(), request.getLevel());
	}

}
