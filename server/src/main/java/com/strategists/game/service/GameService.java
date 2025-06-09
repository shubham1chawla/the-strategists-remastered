package com.strategists.game.service;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.request.GoogleOAuthCredential;

public interface GameService {

    Player createGame(GoogleOAuthCredential credential);

    Game getGameByCode(String code);

    void startGame(Game game);

    Player playTurn(Game game);

    void resetGame(Game game);

    void deleteGame(Game game);

}
