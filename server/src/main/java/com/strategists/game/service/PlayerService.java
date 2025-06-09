package com.strategists.game.service;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.Rent;
import com.strategists.game.entity.Trend;

import java.util.List;

public interface PlayerService {

    List<Player> getPlayersByGame(Game game);

    List<Player> getActivePlayersByGame(Game game);

    List<Player> getPlayersByGameOrderByBankruptcy(Game game);

    Player getPlayerById(long id);

    boolean existsByEmail(String email);

    Player getPlayerByEmail(String email);

    Player addPlayer(Game game, String email, String name);

    Player addPlayer(Game game, String email, String name, boolean host);

    Player kickPlayer(long playerId);

    boolean isTurnAssigned(Game game);

    Player assignTurn(Game game);

    Player getCurrentPlayer(Game game);

    Land movePlayer(Player player, int move);

    Player nextPlayer(Player currentPlayer);

    void skipPlayer(Player player);

    void invest(Player player, Land land, double ownership);

    void payRent(Rent rent);

    void bankruptPlayer(Player player);

    void resetPlayers(Game game);

    List<Trend> updatePlayerTrends(Game game);

}
