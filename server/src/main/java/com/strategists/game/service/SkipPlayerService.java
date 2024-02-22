package com.strategists.game.service;

import com.strategists.game.entity.Game;

public interface SkipPlayerService {

	void schedule(Game game);

	void unschedule(Game game);

}
