package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.response.GameResponse;
import com.strategists.game.update.UpdateType;
import lombok.Getter;

@Getter
public class CreateUpdatePayload implements UpdatePayload<GameResponse> {

    private final Long timestamp = System.currentTimeMillis();
    private final UpdateType type = UpdateType.CREATE;
    private final String gameCode;
    private final Integer gameStep;
    private final Activity activity;
    private final GameResponse payload;

    public CreateUpdatePayload(Activity activity, GameResponse gameResponse) {
        final var game = activity.getGame();
        this.gameCode = game.getCode();
        this.gameStep = game.getCurrentStep();
        this.activity = activity;
        this.payload = gameResponse;
    }

}
