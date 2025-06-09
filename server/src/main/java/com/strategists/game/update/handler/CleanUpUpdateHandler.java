package com.strategists.game.update.handler;

import com.strategists.game.entity.Game;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.CleanUpUpdatePayload;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
public class CleanUpUpdateHandler extends AbstractUpdateHandler<CleanUpUpdatePayload> {

    @Override
    public UpdateType getType() {
        return UpdateType.CLEAN_UP;
    }

    @Override
    public void handle(Object returnValue, Object[] args) {
        // Game instance from argument
        val game = (Game) args[0];

        // Sending update
        sendUpdate(game, new CleanUpUpdatePayload());

        // Closing update SSE Emitters for the game
        closeEmitters(game);
    }

}
