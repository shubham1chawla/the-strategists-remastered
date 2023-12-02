package com.strategists.game.service;

import java.util.List;

import com.strategists.game.entity.Land;
import com.strategists.game.entity.Trend;

public interface LandService {

	List<Land> getLands();

	Land getLandById(long id);

	int getCount();

	Land getLandByIndex(int index);

	void hostEvent(long landId, long eventId, int life, int level);

	void resetLands();

	List<Trend> updateLandTrends();

}
