package com.strategists.game.service;

import com.strategists.game.entity.Player;

public interface PredictionService {

	public enum Prediction {
		WINNER, BANKRUPT, UNKNOWN
	}

	void trainPredictionModel(boolean export);

	Prediction executePredictionModel(Player player);

}
