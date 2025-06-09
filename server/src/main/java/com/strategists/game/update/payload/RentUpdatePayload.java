package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import lombok.Getter;

import java.util.List;

@Getter
public class RentUpdatePayload implements UpdatePayload<List<Player>> {

    private final Activity activity;
    private final List<Player> payload;

    public RentUpdatePayload(Activity activity, Player sourcePlayer, Player targetPlayer) {
        this.activity = activity;
        this.payload = List.of(sourcePlayer, targetPlayer);
    }

    @Override
    public UpdateType getType() {
        return UpdateType.RENT;
    }

}
