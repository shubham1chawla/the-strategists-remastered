package com.strategists.game.repository;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Trend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrendRepository extends JpaRepository<Trend, Long> {

    List<Trend> findByGameOrderByIdAsc(Game game);

    void deleteByGame(Game game);

}
