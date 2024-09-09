package com.strategists.game.update.handler;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.update.UpdateType;
import com.strategists.game.update.payload.AdviceUpdatePayload;

import lombok.val;

@Component
public class AdviceUpdateHandler extends AbstractUpdateHandler<AdviceUpdatePayload> {

	@Override
	public UpdateType getType() {
		return UpdateType.ADVICE;
	}

	@Override
	public void handle(Object returnValue, Object[] args) {
		// Game from the argument and advice returned
		val game = (Game) args[0];

		@SuppressWarnings("unchecked")
		val advices = (List<Advice>) returnValue;
		if (CollectionUtils.isEmpty(advices)) {
			return;
		}

		sendUpdate(game, new AdviceUpdatePayload(advices));
	}

}
