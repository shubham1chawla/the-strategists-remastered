package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.JoinUpdatePayload;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JoinUpdateHandler extends AbstractUpdateHandler<JoinUpdatePayload> {

    private final UpdateType type = UpdateType.JOIN;

    @Override
    public void handle(Object returnValue, Object[] args) {
        // Joined player returned by the method
        final var player = (Player) returnValue;

        // Persisting the activity and sending the update
        final var activity = Activity.ofJoin(player);
        sendUpdate(player.getGame(), new JoinUpdatePayload(saveActivity(activity), player));
    }

}
