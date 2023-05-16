package com.strategists.game.service;

import java.util.List;

import com.strategists.game.entity.Player;

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

	void movePlayer(int move);

	void nextPlayer();

	void buyLand(double ownership);

}
