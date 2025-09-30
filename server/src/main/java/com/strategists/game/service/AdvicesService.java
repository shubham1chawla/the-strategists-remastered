package com.strategists.game.service;

import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;

import java.util.List;

public interface AdvicesService {

    List<Advice> generateAdvices(Game game);

    List<Advice> getAdvicesByGame(Game game);

    List<Advice> markPlayerAdvicesViewed(Player player);

    void clearAdvices(Game game);

    void exportAdvices(Game game);

}
