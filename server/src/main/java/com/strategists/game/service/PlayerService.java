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

	/**
	 * Assigns turn to the next player in sequence. The API returns the current and
	 * previous player references for activity and notification purposes.
	 * 
	 * @return Current and previous player respectively.
	 */
	List<Player> nextPlayer();

	void invest(long playerId, long landId, double ownership);

}
