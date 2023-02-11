package com.strategists.game.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.entity.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {

	boolean existsByUsername(String username);

	boolean existsByTurn(boolean turn);

	Optional<Player> findByTurn(boolean turn);

}
