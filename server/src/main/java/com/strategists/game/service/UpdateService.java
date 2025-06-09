package com.strategists.game.service;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.update.payload.UpdatePayload;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface UpdateService {

    SseEmitter registerEmitter(Player player);

    void closeEmitters(Game game);

    void sendUpdate(Game game, UpdatePayload<?> payload);

    void sendPing();

}
