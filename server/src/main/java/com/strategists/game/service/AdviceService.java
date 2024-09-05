package com.strategists.game.service;

import java.util.List;

import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;

public interface AdviceService {

	List<Advice> generateAdvices(Game game);

	List<Advice> getAdvicesByGame(Game game);

	void clearAdvices(Game game);

}
