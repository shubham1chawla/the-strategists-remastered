package com.strategists.game.service;

import com.strategists.game.entity.Event;

import java.util.List;

public interface EventService {

    List<Event> getEvents();

    Event getEventById(long id);

}
