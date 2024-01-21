package com.strategists.game.service;

import java.util.List;

import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.Rent;
import com.strategists.game.entity.Trend;
import com.strategists.game.request.GoogleLoginRequest;

public interface PlayerService {

	List<Player> getPlayers();

	List<Player> getActivePlayers();

	Player getPlayerById(long id);

	Player getPlayerByEmail(String email);

	Player sendInvite(String email, double cash);

	Player acceptInvite(GoogleLoginRequest request);

	Player kickPlayer(long playerId);

	boolean isTurnAssigned();

	Player assignTurn();

	Player getCurrentPlayer();

	Land movePlayer(Player player, int move);

	Player nextPlayer(Player currentPlayer);

	void invest(Player player, Land land, double ownership);

	void payRent(Rent rent);

	void bankruptPlayer(Player player);

	void resetPlayers();

	List<Trend> updatePlayerTrends();

}
