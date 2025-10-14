package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import lombok.Getter;

import java.util.List;

@Getter
public class TurnUpdatePayload implements UpdatePayload<List<Player>> {

    private final Long timestamp = System.currentTimeMillis();
    private final UpdateType type = UpdateType.TURN;
    private final String gameCode;
    private final Integer gameStep;
    private final Activity activity;
    private final List<Player> payload;

    public TurnUpdatePayload(Activity activity, Player currentPlayer, Player previousPlayer) {
        final var game = activity.getGame();
        this.gameCode = game.getCode();
        this.gameStep = game.getCurrentStep();
        this.activity = activity;
        this.payload = List.of(currentPlayer, previousPlayer);
    }

}
