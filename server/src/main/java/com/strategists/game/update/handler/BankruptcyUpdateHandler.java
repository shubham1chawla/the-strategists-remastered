package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.BankruptcyUpdatePayload;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Getter
@Component
public class BankruptcyUpdateHandler extends AbstractUpdateHandler<BankruptcyUpdatePayload> {

    private final UpdateType type = UpdateType.BANKRUPTCY;

    @Override
    public void handle(Object returnValue, Object[] args) {
        // Bankrupt player as per the argument
        final var player = (Player) args[0];

        // Extracting all the impacted players and lands
        final var players = new HashSet<Player>();
        players.add(player);
        final var lands = player.getPlayerLands().stream().map(PlayerLand::getLand).toList();
        for (Land land : lands) {
            players.addAll(land.getPlayerLands().stream().map(PlayerLand::getPlayer).toList());
        }

        // Persisting the activity and sending the update
        final var activity = Activity.ofBankruptcy(player);
        sendUpdate(player.getGame(), new BankruptcyUpdatePayload(saveActivity(activity), lands, players));
    }

}
