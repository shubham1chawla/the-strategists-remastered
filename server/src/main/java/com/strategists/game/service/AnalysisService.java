package com.strategists.game.service;

import java.util.List;

import com.strategists.game.entity.Player;
import com.strategists.game.entity.Trend;

public interface AnalysisService {

	public enum Prediction {
		WINNER, BANKRUPT, UNKNOWN
	}

	void exportGameData();

	Prediction executePrediction(Player player);

	List<Trend> updateTrends();

}
