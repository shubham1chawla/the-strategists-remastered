package com.strategists.game.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Land;

public interface LandRepository extends JpaRepository<Land, Long> {

	List<Land> findByGameOrderById(Game game);

	long countByGame(Game game);

}
