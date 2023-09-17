package com.strategists.game.activity.handler;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.strategists.game.activity.payload.KickUpdatePayload;
import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Activity.Type;

import lombok.val;

@Component
public class KickActivityHandler implements ActivityHandler<KickUpdatePayload> {

	@Value("${strategists.admin.username}")
	private String adminUsername;

	@Override
	public Optional<KickUpdatePayload> apply(Object obj, Object[] args) {
		val activity = Activity.ofKick(adminUsername, (String) args[0]);
		return Optional.of(new KickUpdatePayload(activity, (String) args[0]));
	}

	@Override
	public Type getType() {
		return Type.KICK;
	}

}
