package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.update.UpdateType;
import lombok.Getter;

import java.util.List;

@Getter
public class AdviceUpdatePayload implements UpdatePayload<List<Advice>> {

    private final Long timestamp = System.currentTimeMillis();
    private final UpdateType type = UpdateType.ADVICE;
    private final String gameCode;
    private final Integer gameStep;
    private final Activity activity = null;
    private final List<Advice> payload;

    public AdviceUpdatePayload(Game game, List<Advice> advices) {
        this.gameCode = game.getCode();
        this.gameStep = game.getCurrentStep();
        this.payload = advices;
    }

}
