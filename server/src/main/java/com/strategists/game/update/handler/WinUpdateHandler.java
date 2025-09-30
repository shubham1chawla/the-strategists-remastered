package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.WinUpdatePayload;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class WinUpdateHandler extends AbstractUpdateHandler<WinUpdatePayload> {

    @Override
    public UpdateType getType() {
        return UpdateType.WIN;
    }

    @Override
    public void handle(Object returnValue, Object[] args) {
        // Winner player returned by the method and game is passed in argument
        final var player = (Player) returnValue;
        final var game = (Game) args[0];

        // If no winner is declared, avoid sending update
        if (Objects.isNull(player)) {

            // Executing prediction model
            publishInferPredictionsModelEvent(game);

            // Generating advice for players
            publishGenerateAdvicesEvent(game);

            // Scheduling player skip event
            scheduleSkipPlayerEvent(game);

            return;
        }

        // Exporting data and training the prediction model
        publishTrainPredictionsModelEvent(game);

        // Exporting advice data
        publishExportAdvicesEvent(game);

        // Persisting the activity and sending the update
        final var activity = Activity.ofWin(player);
        sendUpdate(game, new WinUpdatePayload(saveActivity(activity), player));

        // Removing previously scheduled event
        unscheduleSkipPlayerEvent(game);

        // Scheduling clean-up event
        scheduleCleanUpEvent(game);
    }

}
