package com.strategists.game.activity.handler;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.strategists.game.activity.payload.EndUpdatePayload;
import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.entity.Player;
import com.strategists.game.service.AnalysisService;

import lombok.val;

@Component
public class EndActivityHandler implements ActivityHandler<EndUpdatePayload> {

	@Autowired
	private AnalysisService analysisService;

	@Override
	public Optional<EndUpdatePayload> apply(Object obj, Object[] args) {
		val player = (Player) obj;
		if (Objects.isNull(player)) {
			return Optional.empty();
		}

		// Exporting the game data
		analysisService.exportGameData();

		val activity = Activity.ofEnd(player.getUsername());
		return Optional.of(new EndUpdatePayload(activity, player));
	}

	@Override
	public Type getType() {
		return Type.END;
	}

}
