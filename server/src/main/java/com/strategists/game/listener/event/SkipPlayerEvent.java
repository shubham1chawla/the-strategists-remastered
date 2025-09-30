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
public class SkipPlayerEvent implements SchedulableEvent {

    private String gameCode;
    private Instant scheduledTime;

    @Override
    public String getUniqueIdentifier() {
        return getUniqueIdentifier(gameCode);
    }

    public static SkipPlayerEvent from(Game game) {
        return new SkipPlayerEvent(game.getCode(), Instant.now().plusMillis(game.getSkipPlayerTimeout()));
    }

    public static String getUniqueIdentifier(String gameCode) {
        return "skip-player-" + gameCode;
    }

}
