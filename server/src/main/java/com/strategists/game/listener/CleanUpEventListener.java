package com.strategists.game.listener;

import com.strategists.game.listener.event.CleanUpEvent;
import com.strategists.game.service.GameService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@ConditionalOnProperty(name = "strategists.clean-up.enabled", havingValue = "true")
public class CleanUpEventListener {

    @Autowired
    private GameService gameService;

    @Async
    @EventListener
    public void onEvent(CleanUpEvent event) {
        log.info("Handling CleanUpEvent for game: {}", event.getGameCode());

        // Fetching game
        final var game = gameService.getGameByCode(event.getGameCode());

        // Deleting game
        gameService.deleteGame(game);
    }

}
