package com.strategists.game.listener;

import com.strategists.game.listener.event.SkipPlayerEvent;
import com.strategists.game.service.GameService;
import com.strategists.game.service.PlayerService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@ConditionalOnProperty(name = "strategists.skip-player.enabled", havingValue = "true")
public class SkipPlayerEventListener {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameService gameService;

    @Async
    @EventListener
    @Transactional
    public void onEvent(SkipPlayerEvent event) {
        log.info("Handling SkipPlayerEvent for game: {}", event.getGameCode());

        // Fetching game
        final var game = gameService.getGameByCode(event.getGameCode());

        // Fetching the current player
        final var player = playerService.getCurrentPlayer(game);
        log.info("Skipping {}'s turn in game: {}", player.getUsername(), player.getGameCode());

        // Declaring the player bankrupt if player skipped more than a few times
        playerService.skipPlayer(player);
        if (player.getRemainingSkipsCount() <= 0) {
            playerService.bankruptPlayer(player);
        }

        // Playing next turn
        gameService.playTurn(game);
    }

}
