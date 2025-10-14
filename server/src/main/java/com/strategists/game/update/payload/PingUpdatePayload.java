package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.update.UpdateType;
import lombok.Getter;

@Getter
public class PingUpdatePayload implements UpdatePayload<Object> {

    private final Long timestamp = System.currentTimeMillis();
    private final UpdateType type = UpdateType.PING;
    private final String gameCode = null;
    private final Integer gameStep = null;
    private final Activity activity = null;
    private final Object payload = null;

}
