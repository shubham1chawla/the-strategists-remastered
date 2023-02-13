package com.strategists.game.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.strategists.game.entity.Land;
import com.strategists.game.service.GameService;
import com.strategists.game.service.LandService;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/api/lands")
public class LandController {

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	private class Keys {
		private static final String EVENT_ID = "eventId";
		private static final String LIFE = "life";
		private static final String LEVEL = "level";
	}

	@Autowired
	private GameService gameService;

	@Autowired
	private LandService landService;

	@GetMapping
	public List<Land> getLands() {
		return landService.getLands();
	}

	@PostMapping("/{landId}/events")
	public void hostEvent(@PathVariable long landId, @RequestBody Map<String, Object> map) {
		Assert.isTrue(gameService.isActiveState(), "You need an active game to host events!");
		final long eventId = Integer.toUnsignedLong((int) map.get(Keys.EVENT_ID));
		final int life = (int) map.get(Keys.LIFE);
		final int level = (int) map.get(Keys.LEVEL);
		landService.hostEvent(landId, eventId, life, level);
	}

}
