package com.strategists.game.update.handler;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Trend;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.TrendUpdatePayload;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class TrendUpdateHandler extends AbstractUpdateHandler<TrendUpdatePayload> {

    private final UpdateType type = UpdateType.TREND;

    @Override
    public void handle(Object returnValue, Object[] args) {
        // Game instance provided in the argument
        final var game = (Game) args[0];

        @SuppressWarnings("unchecked") final var trends = (List<Trend>) returnValue;
        sendUpdate(game, new TrendUpdatePayload(game, trends));
    }

}
