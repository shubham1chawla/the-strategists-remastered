package com.strategists.game.service;

import java.util.List;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Prediction;

public interface PredictionService {

	void trainPredictionModel();

	void trainPredictionModel(Game game);

	List<Prediction> executePredictionModel(Game game);

	List<Prediction> getPredictionsByGame(Game game);

	void clearPredictions(Game game);

}
