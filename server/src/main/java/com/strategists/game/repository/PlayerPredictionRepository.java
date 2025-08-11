package com.strategists.game.repository;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.PlayerPrediction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@ConditionalOnProperty(name = "strategists.predictions.enabled", havingValue = "true")
public interface PlayerPredictionRepository extends JpaRepository<PlayerPrediction, Long> {

    List<PlayerPrediction> findByGameOrderById(Game game);

    void deleteByGame(Game game);

}
