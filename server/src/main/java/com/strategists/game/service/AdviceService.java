package com.strategists.game.service;

import java.util.List;

import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;

public interface AdviceService {

	List<Advice> generateAdvices(Game game);

	List<Advice> getAdvicesByGame(Game game);

	List<Advice> markPlayerAdvicesViewed(Player player);

	void clearAdvices(Game game);

	void exportAdvices(Game game);

}
