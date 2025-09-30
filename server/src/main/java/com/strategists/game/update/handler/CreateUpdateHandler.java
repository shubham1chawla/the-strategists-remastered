package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.CreateUpdatePayload;
import org.springframework.stereotype.Component;

@Component
public class CreateUpdateHandler extends AbstractUpdateHandler<CreateUpdatePayload> {

    @Override
    public UpdateType getType() {
        return UpdateType.CREATE;
    }

    @Override
    public void handle(Object returnValue, Object[] args) {
        // Host player returned from the method
        final var player = (Player) returnValue;

        // Sending activity
        sendUpdate(player.getGame(), new CreateUpdatePayload(saveActivity(Activity.ofCreate(player))));

        // Scheduling clean-up event
        scheduleCleanUpEvent(player.getGame());
    }

}
