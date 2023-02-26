package com.strategists.game.service;

import java.util.List;

import com.strategists.game.entity.Player;

public interface PlayerService {

	List<Player> getPlayers();

	Player getPlayerById(long id);

	Player addPlayer(String username, double cash);

	void kickPlayer(String username);

	void assignTurn();

	Player getCurrentPlayer();

	void movePlayer(int move);

	void nextPlayer();

	void buyLand(double ownership);

}
