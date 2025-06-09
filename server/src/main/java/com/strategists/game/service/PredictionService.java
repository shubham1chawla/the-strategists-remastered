package com.strategists.game.service;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Prediction;

import java.util.List;

public interface PredictionService {

    void trainPredictionModel();

    void trainPredictionModel(Game game);

    List<Prediction> executePredictionModel(Game game);

    List<Prediction> getPredictionsByGame(Game game);

    void clearPredictions(Game game);

}
