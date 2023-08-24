package com.strategists.game.activity;

import java.util.Optional;
import java.util.function.BiFunction;

import com.strategists.game.entity.Activity.Type;
import com.strategists.game.update.AbstractUpdatePayload;

public interface ActivityHandler<T extends AbstractUpdatePayload<?>> extends BiFunction<Object, Object[], Optional<T>> {

	public abstract Type getType();

}
