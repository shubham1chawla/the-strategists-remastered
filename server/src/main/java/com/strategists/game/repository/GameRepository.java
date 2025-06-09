package com.strategists.game.repository;

import com.strategists.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, String> {

}
