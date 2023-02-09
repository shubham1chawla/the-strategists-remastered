package com.strategists.game.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.entity.Player;
import com.strategists.game.entity.Player.State;

public interface PlayerRepository extends JpaRepository<Player, Long> {

	List<Player> findByState(State state);

}
