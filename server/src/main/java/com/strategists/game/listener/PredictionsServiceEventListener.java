package com.strategists.game.listener;

import com.strategists.game.listener.event.PredictionsServiceEvent;
import com.strategists.game.service.GameService;
import com.strategists.game.service.PredictionsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Log4j2
@Component
@ConditionalOnProperty(name = "strategists.predictions.enabled", havingValue = "true")
public class PredictionsServiceEventListener {

    @Autowired
    private PredictionsService predictionsService;

    @Autowired
    private GameService gameService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEvent(PredictionsServiceEvent event) {
        log.info("Handling PredictionsServiceEvent[type: {}] for game: {}", event.getType(), event.getGameCode());
        try {
            final var game = gameService.getGameByCode(event.getGameCode());
            switch (event.getType()) {
                case TRAIN -> predictionsService.trainPredictionsModel(game);
                case INFER -> predictionsService.inferPredictionsModel(game);
                default -> log.warn("Unknown PredictionsService Event Type: {}", event.getType());
            }
        } catch (Exception ex) {
            log.error("Failed to invoke PredictionsService for game: {}", event.getGameCode(), ex);
        }
    }

}
