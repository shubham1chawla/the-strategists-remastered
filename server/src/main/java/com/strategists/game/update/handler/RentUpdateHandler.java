package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Rent;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.RentUpdatePayload;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RentUpdateHandler extends AbstractUpdateHandler<RentUpdatePayload> {

    private final UpdateType type = UpdateType.RENT;

    @Override
    public void handle(Object returnValue, Object[] args) {
        // Source player, target player, and other information from argument
        final var rent = (Rent) args[0];
        final var source = rent.getSourcePlayer();
        final var target = rent.getTargetPlayer();

        // Persisting the activity and sending the update
        final var activity = Activity.ofRent(rent);
        sendUpdate(source.getGame(), new RentUpdatePayload(saveActivity(activity), source, target));
    }

}
