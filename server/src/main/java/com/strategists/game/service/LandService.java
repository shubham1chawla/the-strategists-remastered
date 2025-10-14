package com.strategists.game.service;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.GameMap;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.Rent;
import com.strategists.game.entity.Trend;

import java.util.List;

public interface LandService {

    void updateLands(Game game, GameMap gameMap);

    List<Land> getLandsByGame(Game game);

    int getCount(Game game);

    Land getLandByIndex(Game game, int index);

    List<Rent> getPlayerRentsByLand(Player sourcePlayer, Land land);

    void hostEvent(long landId, long eventId, int life, int level);

    void resetLands(Game game);

    List<Trend> updateLandTrends(Game game);

}
