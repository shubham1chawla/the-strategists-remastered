package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.SkipUpdatePayload;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SkipUpdateHandler extends AbstractUpdateHandler<SkipUpdatePayload> {

    private final UpdateType type = UpdateType.SKIP;

    @Override
    public void handle(Object returnValue, Object[] args) {
        // Skipped player as per the argument
        final var player = (Player) args[0];

        // Persisting the activity and sending the update
        final var activity = Activity.ofSkip(player);
        sendUpdate(player.getGame(), new SkipUpdatePayload(saveActivity(activity), player));
    }

}
