package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.listener.event.AdvicesServiceEvent;
import com.strategists.game.listener.event.PredictionsServiceEvent;
import com.strategists.game.service.AdvicesService;
import com.strategists.game.service.PredictionsService;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.WinUpdatePayload;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Getter
@Component
public class WinUpdateHandler extends AbstractUpdateHandler<WinUpdatePayload> {

    private final UpdateType type = UpdateType.WIN;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired(required = false)
    private PredictionsService predictionsService;

    @Autowired(required = false)
    private AdvicesService advicesService;

    @Override
    public void handle(Object returnValue, Object[] args) {
        // Winner player returned by the method and game is passed in argument
        final var player = (Player) returnValue;
        final var game = (Game) args[0];

        // If no winner is declared, avoid sending update
        if (Objects.isNull(player)) {

            // Executing prediction model, if enabled
            if (Objects.nonNull(predictionsService)) {
                eventPublisher.publishEvent(PredictionsServiceEvent.forInfer(game));
            }

            // Generating advice for players, if enabled
            if (Objects.nonNull(advicesService)) {
                eventPublisher.publishEvent(AdvicesServiceEvent.forGenerate(game));
            }

            // Scheduling player skip event
            scheduleSkipPlayerEvent(game);

            return;
        }

        // Persisting the activity and sending the update - This should happen before exporting history
        final var activity = Activity.ofWin(player);
        sendUpdate(game, new WinUpdatePayload(saveActivity(activity), player));

        // Exporting history files - This should happen in sync before invoking the train model event
        exportHistory(game);

        // Training the prediction model, if enabled
        if (Objects.nonNull(predictionsService)) {
            eventPublisher.publishEvent(PredictionsServiceEvent.forTrain(game));
        }

        // Removing previously scheduled event
        unscheduleSkipPlayerEvent(game);

        // Scheduling clean-up event
        scheduleCleanUpEvent(game);
    }

}
