package com.strategists.game.listener.event;

import com.strategists.game.entity.Game;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AdvicesServiceEvent {

    public enum EventType {
        GENERATE, EXPORT
    }

    private EventType type;
    private String gameCode;

    public static AdvicesServiceEvent forGenerate(Game game) {
        return new AdvicesServiceEvent(EventType.GENERATE, game.getCode());
    }

    public static AdvicesServiceEvent forExport(Game game) {
        return new AdvicesServiceEvent(EventType.EXPORT, game.getCode());
    }

}
