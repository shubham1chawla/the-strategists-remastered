package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.PlayerPrediction;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.PredictionUpdatePayload;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Getter
@Component
public class PredictionUpdateHandler extends AbstractUpdateHandler<PredictionUpdatePayload> {

    private static final double WINNER_DIFFERENCE_THRESHOLD = 0.01;

    private final UpdateType type = UpdateType.PREDICTION;

    @Override
    public void handle(Object returnValue, Object[] args) {
        // Game from the argument and predictions returned
        final var game = (Game) args[0];

        @SuppressWarnings("unchecked") final var predictions = (List<PlayerPrediction>) returnValue;
        if (CollectionUtils.isEmpty(predictions)) {
            return;
        }

        /**
         * Sorting the predictions per players' net-worths and winning probabilities.
         * Even if all the predicted probabilities are almost the same, sorting them
         * based on net-worth allows us to bet on the best performing player.
         */
        predictions.sort((p1, p2) -> {
            if (Math.abs(p1.getWinnerProbability() - p2.getWinnerProbability()) < WINNER_DIFFERENCE_THRESHOLD) {
                return p2.getPlayer().getNetWorth() > p1.getPlayer().getNetWorth() ? 1 : -1;
            }
            return p2.getWinnerProbability() > p1.getWinnerProbability() ? 1 : -1;
        });
        final var bestPrediction = predictions.get(0);

        // Persisting the activity and sending the update
        sendUpdate(game, new PredictionUpdatePayload(saveActivity(Activity.ofPrediction(bestPrediction)), predictions));
    }

}
