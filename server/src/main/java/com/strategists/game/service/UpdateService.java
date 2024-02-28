package com.strategists.game.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.update.payload.UpdatePayload;

public interface UpdateService {

	SseEmitter registerEmitter(Player player);

	void sendUpdate(Game game, UpdatePayload<?> payload);

	void sendPing();

}
