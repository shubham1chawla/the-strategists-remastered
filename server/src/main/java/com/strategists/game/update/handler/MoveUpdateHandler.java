package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.MoveUpdatePayload;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class MoveUpdateHandler extends AbstractUpdateHandler<MoveUpdatePayload> {

    private final UpdateType type = UpdateType.MOVE;

    @Override
    public void handle(Object returnValue, Object[] args) {
        // Moving player and move amount from argument with associated land returned
        final var player = (Player) args[0];
        final var move = (int) args[1];
        final var land = (Land) returnValue;

        // Persisting the activity and sending the update
        final var activity = Activity.ofMove(player, move, land);
        sendUpdate(player.getGame(), new MoveUpdatePayload(saveActivity(activity), player));
    }

}
