package com.strategists.game.repository;

import com.strategists.game.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByOrderByFactorAsc();

}
