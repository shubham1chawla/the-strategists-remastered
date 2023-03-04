package com.strategists.game.service;

import java.util.List;

import com.strategists.game.entity.Event;

public interface EventService {

	List<Event> getEvents();

	Event getEventById(long id);

}
