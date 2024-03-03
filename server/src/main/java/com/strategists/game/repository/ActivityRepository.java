package com.strategists.game.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Game;
import com.strategists.game.update.UpdateType;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

	List<Activity> findByGameOrderByIdDesc(Game game);

	List<Activity> findByGameAndTypeOrderById(Game game, UpdateType type);

	long deleteByGame(Game game);

}
