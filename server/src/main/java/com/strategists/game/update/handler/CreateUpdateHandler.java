package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.response.GameResponse;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.CreateUpdatePayload;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CreateUpdateHandler extends AbstractUpdateHandler<CreateUpdatePayload> {

    private final UpdateType type = UpdateType.CREATE;

    @Override
    public void handle(Object returnValue, Object[] args) {
        // GameResponse instance from return value
        final var gameResponse = (GameResponse) returnValue;
        final var game = gameResponse.getGame();
        final var hostPlayer = gameResponse.getHostPlayer();

        // Sending activity
        final var activity = saveActivity(Activity.ofCreate(hostPlayer));
        sendUpdate(game, new CreateUpdatePayload(activity, gameResponse));

        // Scheduling clean-up event
        scheduleCleanUpEvent(game);
    }

}
