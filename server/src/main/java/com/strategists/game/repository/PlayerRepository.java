package com.strategists.game.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.entity.Player;
import com.strategists.game.entity.Player.State;

public interface PlayerRepository extends JpaRepository<Player, Long> {

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	Optional<Player> findByEmail(String email);

	boolean existsByTurn(boolean turn);

	Optional<Player> findByTurn(boolean turn);

	List<Player> findByState(State state);

}
