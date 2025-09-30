package com.strategists.game.listener.event;

import com.strategists.game.entity.Game;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PredictionsServiceEvent {

    public enum EventType {
        TRAIN, INFER
    }

    private EventType type;
    private String gameCode;

    public static PredictionsServiceEvent forTrain(Game game) {
        return new PredictionsServiceEvent(EventType.TRAIN, game.getCode());
    }

    public static PredictionsServiceEvent forInfer(Game game) {
        return new PredictionsServiceEvent(EventType.INFER, game.getCode());
    }

}
