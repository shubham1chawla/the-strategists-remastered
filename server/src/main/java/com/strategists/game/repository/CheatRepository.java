package com.strategists.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.entity.Cheat;

public interface CheatRepository extends JpaRepository<Cheat, Long> {
	// No specialized methods required
}
