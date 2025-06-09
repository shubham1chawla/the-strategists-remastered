package com.strategists.game.update.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.update.UpdateType;

public interface UpdatePayload<T> {

    UpdateType getType();

    Activity getActivity();

    T getPayload();

}
