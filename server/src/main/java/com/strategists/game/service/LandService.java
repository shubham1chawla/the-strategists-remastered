package com.strategists.game.service;

import java.util.List;

import com.strategists.game.entity.Land;

public interface LandService {

	List<Land> getLands();

	int getCount();

	Land getLandByIndex(int index);
	
	void hostEvent(long landId, long eventId, int life, int level);

}
