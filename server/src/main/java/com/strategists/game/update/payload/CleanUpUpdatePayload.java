package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Game;
import com.strategists.game.update.UpdateType;
import lombok.Getter;

@Getter
public class CleanUpUpdatePayload implements UpdatePayload<Object> {

    private final Long timestamp = System.currentTimeMillis();
    private final UpdateType type = UpdateType.CLEAN_UP;
    private final String gameCode;
    private final Integer gameStep;
    private final Activity activity = null;
    private final Object payload = null;

    public CleanUpUpdatePayload(Game game) {
        this.gameCode = game.getCode();
        this.gameStep = game.getCurrentStep();
    }

}
