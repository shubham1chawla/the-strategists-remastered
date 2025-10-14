package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.TurnUpdatePayload;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Getter
@Component
public class TurnUpdateHandler extends AbstractUpdateHandler<TurnUpdatePayload> {

    private final UpdateType type = UpdateType.TURN;

    @Override
    public void handle(Object returnValue, Object[] args) {
        // Checking if valid current player exists
        if (Objects.isNull(returnValue)) {
            return;
        }
        final var current = (Player) returnValue;
        final var previous = (Player) args[0];

        // Persisting the activity and sending the update
        final var activity = Activity.ofTurn(previous, current);
        sendUpdate(current.getGame(), new TurnUpdatePayload(saveActivity(activity), current, previous));
    }

}
