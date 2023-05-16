package com.strategists.game.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.entity.Player;
import com.strategists.game.entity.Player.State;

public interface PlayerRepository extends JpaRepository<Player, Long> {

	boolean existsByUsername(String username);

	Optional<Player> findByUsername(String username);

	void deleteByUsername(String username);

	boolean existsByTurn(boolean turn);

	Optional<Player> findByTurn(boolean turn);

	List<Player> findByStateIn(Set<State> states);

}
