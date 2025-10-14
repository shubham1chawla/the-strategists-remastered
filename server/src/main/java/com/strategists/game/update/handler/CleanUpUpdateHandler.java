package com.strategists.game.update.handler;

import com.strategists.game.entity.Game;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.CleanUpUpdatePayload;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CleanUpUpdateHandler extends AbstractUpdateHandler<CleanUpUpdatePayload> {

    private final UpdateType type = UpdateType.CLEAN_UP;

    @Override
    public void handle(Object returnValue, Object[] args) {
        // Game instance from argument
        final var game = (Game) args[0];

        // Sending update
        sendUpdate(game, new CleanUpUpdatePayload(game));

        // Closing update SSE Emitters for the game
        closeEmitters(game);
    }

}
