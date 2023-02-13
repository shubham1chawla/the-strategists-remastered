package com.strategists.game.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.entity.Event;
import com.strategists.game.repository.EventRepository;
import com.strategists.game.service.EventService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class EventServiceImpl implements EventService {

	@Autowired
	private EventRepository eventRepository;

	@Override
	public List<Event> getEvents() {
		return eventRepository.findByOrderByFactorAsc();
	}

	@Override
	public Event getEventById(long id) {
		final Optional<Event> opt = eventRepository.findById(id);
		Assert.isTrue(opt.isPresent(), "No event found with ID: " + id);

		log.info("Found event: {}", opt.get());
		return opt.get();
	}

}
