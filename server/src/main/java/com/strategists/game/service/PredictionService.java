package com.strategists.game.service;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;

public interface PredictionService {

	public enum Prediction {
		WINNER, BANKRUPT, UNKNOWN
	}

	/**
	 * Trains the prediction model with existing data without exporting any
	 * additional game's data.
	 */
	void trainPredictionModel();

	/**
	 * Exports the provided game's data and then trains the prediction model with
	 * new and existing data.
	 * 
	 * @param game
	 */
	void trainPredictionModel(Game game);

	Prediction executePredictionModel(Player player);

}
