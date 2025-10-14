package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.response.GameResponse;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.ResetUpdatePayload;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ResetUpdateHandler extends AbstractUpdateHandler<ResetUpdatePayload> {

    private final UpdateType type = UpdateType.RESET;

    @Override
    @Transactional
    public void handle(Object returnValue, Object[] args) {
        // GameResponse instance from return value
        final var gameResponse = (GameResponse) returnValue;
        final var game = gameResponse.getGame();

        // Sending activity
        final var activity = saveActivity(Activity.ofReset(game));
        sendUpdate(game, new ResetUpdatePayload(activity, gameResponse));

        // Removing previously scheduled event
        unscheduleSkipPlayerEvent(game);

        // Scheduling clean-up event
        scheduleCleanUpEvent(game);
    }

}
