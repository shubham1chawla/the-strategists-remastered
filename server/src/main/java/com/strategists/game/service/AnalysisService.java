package com.strategists.game.service;

import java.util.List;

import com.strategists.game.entity.Trend;

public interface AnalysisService {

	void exportGameData();

	List<Trend> updateTrends();

}
