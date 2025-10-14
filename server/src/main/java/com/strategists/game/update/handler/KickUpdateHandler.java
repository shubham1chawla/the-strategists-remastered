package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.KickUpdatePayload;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Getter
@Component
public class KickUpdateHandler extends AbstractUpdateHandler<KickUpdatePayload> {

    private final UpdateType type = UpdateType.KICK;

    @Override
    public void handle(Object returnValue, Object[] args) {
        final var player = (Player) returnValue;
        if (Objects.isNull(player)) {
            return;
        }

        // Persisting the activity and sending the update
        final var activity = Activity.ofKick(player);
        sendUpdate(player.getGame(), new KickUpdatePayload(saveActivity(activity), player.getId()));
    }

}
