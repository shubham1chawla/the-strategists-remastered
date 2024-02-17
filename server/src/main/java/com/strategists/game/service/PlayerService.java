package com.strategists.game.service;

import java.util.List;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.Rent;
import com.strategists.game.entity.Trend;
import com.strategists.game.request.GoogleLoginRequest;

public interface PlayerService {

	List<Player> getPlayersByGame(Game game);

	List<Player> getActivePlayersByGame(Game game);

	List<Player> getPlayersByGameOrderByBankruptcy(Game game);

	Player getPlayerById(long id);

	Player getPlayerByEmail(String email);

	Player getPlayerByUsername(Game game, String username);

	Player sendInvite(Game game, String email, double cash);

	Player acceptInvite(GoogleLoginRequest request);

	Player kickPlayer(long playerId);

	boolean isTurnAssigned(Game game);

	Player assignTurn(Game game);

	Player getCurrentPlayer(Game game);

	Land movePlayer(Player player, int move);

	Player nextPlayer(Player currentPlayer);

	void invest(Player player, Land land, double ownership);

	void payRent(Rent rent);

	void bankruptPlayer(Player player);

	void resetPlayers(Game game);

	List<Trend> updatePlayerTrends(Game game);

}
