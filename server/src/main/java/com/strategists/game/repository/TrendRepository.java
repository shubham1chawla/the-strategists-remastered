package com.strategists.game.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Trend;

public interface TrendRepository extends JpaRepository<Trend, Long> {

	List<Trend> findByGameOrderByIdAsc(Game game);

	long deleteByGame(Game game);

}
