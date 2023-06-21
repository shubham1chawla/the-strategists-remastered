package com.strategists.game.service.impl;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.entity.Event;
import com.strategists.game.entity.Land;
import com.strategists.game.repository.LandRepository;
import com.strategists.game.service.EventService;
import com.strategists.game.service.LandService;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class LandServiceImpl implements LandService {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private LandRepository landRepository;

	@Autowired
	private EventService eventService;

	/**
	 * Caching the count of lands to avoid DB calls on runtime. We can't cache the
	 * entire list of lands as many of its fields are transient and subject to
	 * change.
	 */
	private Integer count;

	@PostConstruct
	public void setup() {
		count = (int) landRepository.count();
	}

	@Override
	public List<Land> getLands() {
		return landRepository.findAll();
	}

	@Override
	@Transactional
	public Land getLandById(long id) {
		Optional<Land> opt = landRepository.findById(id);
		Assert.isTrue(opt.isPresent(), "No land found with ID: " + id);

		/*
		 * Since we update player's entity with player-land information after any
		 * investment, we need to refresh land's entity to reflect updated information
		 * in current transactional session, subsequent calls will reflect updated
		 * information regardless.
		 */
		val land = opt.get();
		em.refresh(land);

		log.info("Found land: {}", land);
		return land;
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public Land getLandByIndex(int index) {
		return getLands().get(index);
	}

	@Override
	public void hostEvent(long landId, long eventId, int life, int level) {
		final Optional<Land> opt = landRepository.findById(landId);
		Assert.isTrue(opt.isPresent(), "No land associated with ID: " + landId);

		final Land land = opt.get();
		final Event event = eventService.getEventById(eventId);

		land.addEvent(event, life, level);
		landRepository.save(land);

		log.info("Event {} hosted on {}.", event.getName(), land.getName());
	}

}
