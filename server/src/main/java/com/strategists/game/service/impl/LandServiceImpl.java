package com.strategists.game.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.entity.Land;
import com.strategists.game.repository.LandRepository;
import com.strategists.game.service.EventService;
import com.strategists.game.service.LandService;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class LandServiceImpl implements LandService {

	@Autowired
	private LandRepository landRepository;

	@Autowired
	private EventService eventService;

	@Override
	public List<Land> getLands() {
		return landRepository.findAll();
	}

	@Override
	public Land getLandById(long id) {
		val opt = landRepository.findById(id);
		Assert.isTrue(opt.isPresent(), "No land found with ID: " + id);

		return opt.get();
	}

	@Override
	public int getCount() {
		return (int) landRepository.count();
	}

	@Override
	public Land getLandByIndex(int index) {
		return getLands().get(index);
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
	public void resetLands() {
		val lands = getLands();
		for (Land land : lands) {
			land.getLandEvents().clear();
		}

		landRepository.saveAll(lands);
		log.info("Reset lands completed");
	}

}
