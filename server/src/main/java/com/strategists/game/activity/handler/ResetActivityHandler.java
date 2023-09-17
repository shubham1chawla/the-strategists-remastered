package com.strategists.game.activity.handler;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.strategists.game.activity.payload.ResetUpdatePayload;
import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Activity.Type;

@Component
public class ResetActivityHandler implements ActivityHandler<ResetUpdatePayload> {

	@Value("${strategists.admin.username}")
	private String adminUsername;

	@Override
	public Optional<ResetUpdatePayload> apply(Object t, Object[] u) {
		return Optional.of(new ResetUpdatePayload(Activity.ofReset(adminUsername)));
	}

	@Override
	public Type getType() {
		return Type.RESET;
	}

	@Override
	public boolean shouldPersistActivity() {
		return false;
	}

}
