package com.strategists.game.listener;

import com.strategists.game.listener.event.AdvicesServiceEvent;
import com.strategists.game.service.AdvicesService;
import com.strategists.game.service.GameService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

@Log4j2
@Component
@ConditionalOnProperty(name = "strategists.advices.enabled", havingValue = "true")
public class AdvicesServiceEventListener {

    @Autowired
    private AdvicesService advicesService;

    @Autowired
    private GameService gameService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEvent(AdvicesServiceEvent event) {
        log.info("Handling AdvicesServiceEvent[type: {}] for game: {}", event.getType(), event.getGameCode());
        try {
            final var game = gameService.getGameByCode(event.getGameCode());
            if (Objects.requireNonNull(event.getType()) == AdvicesServiceEvent.EventType.GENERATE) {
                advicesService.generateAdvices(game);
            } else {
                log.warn("Unknown AdvicesService Event Type: {}", event.getType());
            }
        } catch (Exception ex) {
            log.error("Failed to invoke AdvicesService for game: {}", event.getGameCode(), ex);
        }
    }

}
