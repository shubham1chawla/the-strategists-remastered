package com.strategists.game.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Trend;
import com.strategists.game.repository.LandRepository;
import com.strategists.game.repository.TrendRepository;
import com.strategists.game.service.EventService;
import com.strategists.game.service.LandService;
import com.strategists.game.update.UpdateMapping;
import com.strategists.game.update.UpdateType;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class LandServiceImpl implements LandService {

	@Autowired
	private LandRepository landRepository;

	@Autowired
	private EventService eventService;

	@Autowired
	private TrendRepository trendRepository;

	@Override
	public void save(List<Land> lands) {
		landRepository.saveAll(lands);
	}

	@Override
	public List<Land> getLandsByGame(Game game) {
		return landRepository.findByGame(game);
	}

	@Override
	public int getCount(Game game) {
		return (int) landRepository.countByGame(game);
	}

	@Override
	public Land getLandByIndex(Game game, int index) {
		return getLandsByGame(game).get(index);
	}

	@Override
	public void hostEvent(long landId, long eventId, int life, int level) {
		val opt = landRepository.findById(landId);
		Assert.isTrue(opt.isPresent(), "No land associated with ID: " + landId);

		val land = opt.get();
		val event = eventService.getEventById(eventId);

		land.addEvent(event, life, level);
		landRepository.save(land);

		log.info("Event {} hosted on {}.", event.getName(), land.getName());
	}

	@Override
	public void resetLands(Game game) {
		val lands = getLandsByGame(game);
		for (Land land : lands) {
			land.getLandEvents().clear();
		}

		landRepository.saveAll(lands);
		log.info("Reseted lands for game ID: {}", game.getId());
	}

	@Override
	@UpdateMapping(UpdateType.TREND)
	public List<Trend> updateLandTrends(Game game) {
		return trendRepository.saveAll(getLandsByGame(game).stream().map(Trend::fromLand).toList());
	}

}
