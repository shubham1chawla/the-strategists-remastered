package com.strategists.game.service;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.request.GoogleOAuthCredential;
import com.strategists.game.response.GameResponse;

public interface GameService {

    GameResponse createGame(GoogleOAuthCredential credential);

    Game getGameByCode(String code);

    GameResponse getGameResponseByGame(Game game);

    void startGame(Game game);

    Player playTurn(Game game);

    GameResponse resetGame(Game game);

    void deleteGame(Game game);

}
