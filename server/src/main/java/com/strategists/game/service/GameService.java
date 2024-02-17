package com.strategists.game.service;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.GameMap;
import com.strategists.game.entity.Player;

public interface GameService {

	Game createGame(String adminUsername, String adminEmail, GameMap gameMap);

	Game getGameByAdminEmail(String adminEmail);

	Game getGameById(long id);

	void startGame(Game game);

	Player playTurn(Game game);

	void resetGame(Game game);

}
