package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.SkipUpdatePayload;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
public class SkipUpdateHandler extends AbstractUpdateHandler<SkipUpdatePayload> {

    @Override
    public UpdateType getType() {
        return UpdateType.SKIP;
    }

    @Override
    public void handle(Object returnValue, Object[] args) {
        // Skipped player as per the argument
        val player = (Player) args[0];

        // Persisting the activity and sending the update
        val activity = Activity.ofSkip(player);
        sendUpdate(player.getGame(), new SkipUpdatePayload(saveActivity(activity), player));
    }

}
