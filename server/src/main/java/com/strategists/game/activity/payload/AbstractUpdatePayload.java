package com.strategists.game.activity.payload;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Activity.Type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractUpdatePayload<T> {

	private Activity activity;
	private T data;

	public Type getType() {
		return activity.getType();
	}

}
