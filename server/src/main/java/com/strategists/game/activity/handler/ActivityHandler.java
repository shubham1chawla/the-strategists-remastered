package com.strategists.game.activity.handler;

import java.util.Optional;
import java.util.function.BiFunction;

import com.strategists.game.activity.payload.AbstractUpdatePayload;
import com.strategists.game.entity.Activity.Type;

public interface ActivityHandler<T extends AbstractUpdatePayload<?>> extends BiFunction<Object, Object[], Optional<T>> {

	Type getType();

	default boolean shouldPersistActivity() {
		return true;
	}

}
