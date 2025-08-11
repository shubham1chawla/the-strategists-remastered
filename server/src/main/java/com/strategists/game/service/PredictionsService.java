package com.strategists.game.service;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.PlayerPrediction;

import java.util.List;

public interface PredictionsService {

    void trainPredictionsModel(Game game);

    List<PlayerPrediction> inferPredictionsModel(Game game);

    List<PlayerPrediction> getPlayerPredictionsByGame(Game game);

    void clearPlayerPredictions(Game game);

}
