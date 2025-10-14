package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.update.UpdateType;
import lombok.Getter;

@Getter
public class KickUpdatePayload implements UpdatePayload<Long> {

    private final Long timestamp = System.currentTimeMillis();
    private final UpdateType type = UpdateType.KICK;
    private final String gameCode;
    private final Integer gameStep;
    private final Activity activity;
    private final Long payload;

    public KickUpdatePayload(Activity activity, Long kickedPlayerId) {
        final var game = activity.getGame();
        this.gameCode = game.getCode();
        this.gameStep = game.getCurrentStep();
        this.activity = activity;
        this.payload = kickedPlayerId;
    }

}
