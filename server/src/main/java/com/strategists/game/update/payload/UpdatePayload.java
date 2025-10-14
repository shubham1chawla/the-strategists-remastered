package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.update.UpdateType;

public interface UpdatePayload<T> {

    Long getTimestamp();

    UpdateType getType();

    String getGameCode();

    Integer getGameStep();

    Activity getActivity();

    T getPayload();

}
