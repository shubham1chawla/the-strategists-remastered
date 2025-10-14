package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class InvestUpdatePayload implements UpdatePayload<Map<String, Object>> {

    private final Long timestamp = System.currentTimeMillis();
    private final UpdateType type = UpdateType.INVEST;
    private final String gameCode;
    private final Integer gameStep;
    private final Activity activity;
    private final Map<String, Object> payload;

    public InvestUpdatePayload(Activity activity, Land investedLand, List<Player> investors) {
        final var game = activity.getGame();
        this.gameCode = game.getCode();
        this.gameStep = game.getCurrentStep();
        this.activity = activity;
        this.payload = Map.of("land", investedLand, "players", investors);
    }

}
