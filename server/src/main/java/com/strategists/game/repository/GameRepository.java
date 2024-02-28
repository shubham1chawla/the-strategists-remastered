package com.strategists.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.entity.Game;

public interface GameRepository extends JpaRepository<Game, String> {

}
