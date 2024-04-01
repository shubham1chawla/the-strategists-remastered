package com.strategists.game.repository;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Prediction;

@ConditionalOnProperty(name = "strategists.prediction.enabled", havingValue = "true")
public interface PredictionRepository extends JpaRepository<Prediction, Long> {

	List<Prediction> findByGameOrderById(Game game);

	long deleteByGame(Game game);

}
