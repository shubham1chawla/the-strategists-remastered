package com.strategists.game.activity.handler;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.strategists.game.activity.payload.StartUpdatePayload;
import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Player;
import com.strategists.game.service.AnalysisService;

import lombok.val;

@Component
public class StartActivityHandler implements ActivityHandler<StartUpdatePayload> {

	@Value("${strategists.admin.username}")
	private String adminUsername;

	@Autowired
	private AnalysisService analysisService;

	@Override
	public Optional<StartUpdatePayload> apply(Object obj, Object[] args) {
		val player = (Player) obj;

		// Adding initial game trends
		analysisService.updateTrends();

		val activity = Activity.ofStart(adminUsername, player.getUsername());
		return Optional.of(new StartUpdatePayload(activity, player));
	}

	@Override
	public Type getType() {
		return Type.START;
	}

}
