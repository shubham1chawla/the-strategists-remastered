package com.strategists.game.update.handler;

import com.strategists.game.update.UpdateType;

public interface UpdateHandler {

	UpdateType getType();

	void handle(Object returnValue, Object[] args);

}
