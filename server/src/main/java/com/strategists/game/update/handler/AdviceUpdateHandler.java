package com.strategists.game.update.handler;

import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.AdviceUpdatePayload;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class AdviceUpdateHandler extends AbstractUpdateHandler<AdviceUpdatePayload> {

    @Override
    public UpdateType getType() {
        return UpdateType.ADVICE;
    }

    @Override
    public void handle(Object returnValue, Object[] args) {
        // Game from the argument and advice returned
        Game game = null;
        if (args[0].getClass().isAssignableFrom(Game.class)) {
            game = (Game) args[0];
        } else if (args[0].getClass().isAssignableFrom(Player.class)) {
            game = ((Player) args[0]).getGame();
        }
        Assert.notNull(game, "Unable to extract game from arguments!");

        @SuppressWarnings("unchecked")
        val advices = (List<Advice>) returnValue;
        if (CollectionUtils.isEmpty(advices)) {
            return;
        }

        sendUpdate(game, new AdviceUpdatePayload(advices));
    }

}
