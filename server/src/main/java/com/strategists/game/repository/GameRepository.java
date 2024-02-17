package com.strategists.game.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.entity.Game;

public interface GameRepository extends JpaRepository<Game, Long> {

	Optional<Game> findByAdminEmail(String adminEmail);

	boolean existsByAdminEmail(String adminEmail);

}
