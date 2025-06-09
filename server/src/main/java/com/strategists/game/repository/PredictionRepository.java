package com.strategists.game.repository;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Prediction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@ConditionalOnProperty(name = "strategists.prediction.enabled", havingValue = "true")
public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    List<Prediction> findByGameOrderById(Game game);

    void deleteByGame(Game game);

}
