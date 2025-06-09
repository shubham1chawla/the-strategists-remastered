package com.strategists.game.repository;

import com.strategists.game.entity.Cheat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheatRepository extends JpaRepository<Cheat, Long> {
    // No specialized methods required
}
