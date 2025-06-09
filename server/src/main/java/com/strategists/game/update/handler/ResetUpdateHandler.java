package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Game;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.ResetUpdatePayload;
import jakarta.transaction.Transactional;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
public class ResetUpdateHandler extends AbstractUpdateHandler<ResetUpdatePayload> {

    @Override
    public UpdateType getType() {
        return UpdateType.RESET;
    }

    @Override
    @Transactional
    public void handle(Object returnValue, Object[] args) {
        // Game instance from argument
        val game = (Game) args[0];

        // Resetting activities and trends
        reset(game);

        // Sending activity
        sendUpdate(game, new ResetUpdatePayload(saveActivity(Activity.ofReset(game))));

        // Removing previously scheduled task
        unscheduleSkipPlayerTask(game);

        // Scheduling clean-up task
        scheduleCleanUpTask(game);
    }

}
