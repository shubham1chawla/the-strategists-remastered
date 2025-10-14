package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.InvestUpdatePayload;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class InvestUpdateHandler extends AbstractUpdateHandler<InvestUpdatePayload> {

    private final UpdateType type = UpdateType.INVEST;

    @Override
    public void handle(Object returnValue, Object[] args) {
        // Investing player, invested land, and ownership amount from argument
        final var player = (Player) args[0];
        final var land = (Land) args[1];
        final var ownership = (double) args[2];

        /*
         * Updating all the players that are linked with this land. Each player's
         * net-worth is tied with the market value of the land, therefore investment in
         * this land will boost each investors' net-worth.
         */
        final var players = land.getPlayerLands().stream().map(PlayerLand::getPlayer).toList();

        // Persisting the activity and sending the update
        final var activity = Activity.ofInvest(player, land, ownership);
        sendUpdate(player.getGame(), new InvestUpdatePayload(saveActivity(activity), land, players));
    }

}
