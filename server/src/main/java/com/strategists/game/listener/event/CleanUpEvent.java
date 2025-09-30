package com.strategists.game.listener.event;

import com.strategists.game.entity.Game;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CleanUpEvent implements SchedulableEvent {

    private String gameCode;
    private Instant scheduledTime;

    @Override
    public String getUniqueIdentifier() {
        return getUniqueIdentifier(gameCode);
    }

    public static CleanUpEvent from(Game game) {
        return new CleanUpEvent(game.getCode(), Instant.now().plusMillis(game.getCleanUpDelay()));
    }

    public static String getUniqueIdentifier(String gameCode) {
        return "clean-up-" + gameCode;
    }

}
