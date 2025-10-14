package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import lombok.Getter;

@Getter
public class SkipUpdatePayload implements UpdatePayload<Player> {

    private final Long timestamp = System.currentTimeMillis();
    private final UpdateType type = UpdateType.SKIP;
    private final String gameCode;
    private final Integer gameStep;
    private final Activity activity;
    private final Player payload;

    public SkipUpdatePayload(Activity activity, Player skippedPlayer) {
        final var game = activity.getGame();
        this.gameCode = game.getCode();
        this.gameStep = game.getCurrentStep();
        this.activity = activity;
        this.payload = skippedPlayer;
    }

}
