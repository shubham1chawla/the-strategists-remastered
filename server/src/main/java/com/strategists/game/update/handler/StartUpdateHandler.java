package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.StartUpdatePayload;
import org.springframework.stereotype.Component;

@Component
public class StartUpdateHandler extends AbstractUpdateHandler<StartUpdatePayload> {

    @Override
    public UpdateType getType() {
        return UpdateType.START;
    }

    @Override
    public void handle(Object returnValue, Object[] args) {
        // Starting player returned from the method
        final var player = (Player) returnValue;

        // Persisting the activity and sending the update
        final var activity = Activity.ofStart(player);
        sendUpdate(player.getGame(), new StartUpdatePayload(saveActivity(activity), player));

        // Scheduling player skip event
        scheduleSkipPlayerEvent(player.getGame());

        // Removing previously scheduled event
        unscheduleCleanUpEvent(player.getGame());
    }

}
