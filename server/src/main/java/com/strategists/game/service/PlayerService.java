package com.strategists.game.service;

import java.util.List;

import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.Rent;

public interface PlayerService {

	long getCount();

	List<Player> getPlayers();

	Player getPlayerById(long id);

	Player getPlayerByUsername(String username);

	Player addPlayer(String username, double cash);

	void kickPlayer(String username);

	boolean isTurnAssigned();

	void assignTurn();

	Player getCurrentPlayer();

	Land movePlayer(Player player, int move);

	Player nextPlayer(Player currentPlayer);

	void invest(long playerId, long landId, double ownership);

	void payRent(Rent rent);

	void bankruptPlayer(Player player);

}
