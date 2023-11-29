package com.strategists.game.activity.handler;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.strategists.game.activity.payload.ResetUpdatePayload;
import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.repository.TrendRepository;

@Component
public class ResetActivityHandler implements ActivityHandler<ResetUpdatePayload> {

	@Value("${strategists.admin.username}")
	private String adminUsername;

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private TrendRepository trendRepository;

	@Override
	public Optional<ResetUpdatePayload> apply(Object obj, Object[] args) {

		// Reseting activities
		activityRepository.deleteAll();

		// Reseting trends
		trendRepository.deleteAll();

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
