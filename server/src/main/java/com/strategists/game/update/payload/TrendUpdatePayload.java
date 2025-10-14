package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Trend;
import com.strategists.game.update.UpdateType;
import lombok.Getter;

import java.util.List;

@Getter
public class TrendUpdatePayload implements UpdatePayload<List<Trend>> {

    private final Long timestamp = System.currentTimeMillis();
    private final UpdateType type = UpdateType.TREND;
    private final String gameCode;
    private final Integer gameStep;
    private final Activity activity = null;
    private final List<Trend> payload;

    public TrendUpdatePayload(Game game, List<Trend> trends) {
        this.gameCode = game.getCode();
        this.gameStep = game.getCurrentStep();
        this.payload = trends;
    }

}
