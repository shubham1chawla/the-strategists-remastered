package com.strategists.game.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

	List<Event> findByOrderByFactorAsc();

}
