package com.strategists.game.update;

import com.strategists.game.entity.Activity.Type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractUpdatePayload<T> {

	private Type type;
	private T data;

}
