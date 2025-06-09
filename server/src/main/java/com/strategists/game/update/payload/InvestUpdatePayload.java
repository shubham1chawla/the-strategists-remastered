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

    private final Activity activity;
    private final Map<String, Object> payload;

    public InvestUpdatePayload(Activity activity, Land land, List<Player> players) {
        this.activity = activity;
        this.payload = Map.of("land", land, "players", players);
    }

    @Override
    public UpdateType getType() {
        return UpdateType.INVEST;
    }

}
