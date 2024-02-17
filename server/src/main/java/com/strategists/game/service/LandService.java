package com.strategists.game.service;

import java.util.List;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Trend;

public interface LandService {

	void save(List<Land> lands);

	List<Land> getLandsByGame(Game game);

	int getCount(Game game);

	Land getLandByIndex(Game game, int index);

	void hostEvent(long landId, long eventId, int life, int level);

	void resetLands(Game game);

	List<Trend> updateLandTrends(Game game);

}
