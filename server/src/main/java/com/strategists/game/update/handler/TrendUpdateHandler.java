package com.strategists.game.update.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Trend;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.TrendUpdatePayload;

import lombok.val;

@Component
public class TrendUpdateHandler extends AbstractUpdateHandler<TrendUpdatePayload> {

	@Override
	public UpdateType getType() {
		return UpdateType.TREND;
	}

	@Override
	public void handle(Object returnValue, Object[] args) {
		// Game instance provided in the argument
		val game = (Game) args[0];

		@SuppressWarnings("unchecked")
		val trends = (List<Trend>) returnValue;
		sendUpdate(game, new TrendUpdatePayload(trends));
	}

}
