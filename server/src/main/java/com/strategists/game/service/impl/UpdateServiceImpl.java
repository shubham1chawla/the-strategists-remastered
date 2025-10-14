package com.strategists.game.service.impl;

import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.service.UpdateService;
import com.strategists.game.update.payload.PingUpdatePayload;
import com.strategists.game.update.payload.UpdatePayload;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

@Log4j2
@Service
public class UpdateServiceImpl implements UpdateService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter registerEmitter(Player player) {
        final var emitterKey = getEmitterKey(player);
        emitters.computeIfAbsent(emitterKey, key -> {
            final var emitter = new SseEmitter(-1L);
            emitter.onCompletion(() -> emitters.remove(key));
            emitter.onError(ex -> emitters.remove(key));
            return emitter;
        });
        return emitters.get(emitterKey);
    }

    @Override
    public void closeEmitters(Game game) {
        emitters.forEach((key, value) -> {
            if (!filterByGame(game, key)) {
                return;
            }
            value.complete();
        });
    }

    @Override
    public void sendUpdate(Game game, UpdatePayload<?> payload) {
        sendUpdate(emitterKey -> filterByGame(game, emitterKey), payload);
    }

    @Override
    public void sendPing() {
        sendUpdate(emitterKey -> true, new PingUpdatePayload());
    }

    private void sendUpdate(Predicate<String> predicate, UpdatePayload<?> payload) {
        emitters.entrySet().stream().filter(entry -> predicate.test(entry.getKey())).forEach(entry -> {
            try {
                entry.getValue().send(payload, MediaType.APPLICATION_JSON);
            } catch (IOException ex) {
                log.error(ex.getMessage());
                log.debug(ex);
            }
        });
    }

    private String getEmitterKey(Player player) {
        return String.format("game-%s-player-%s", player.getGame().getCode(), player.getId());
    }

    private boolean filterByGame(Game game, String emitterKey) {
        final var prefix = String.format("game-%s", game.getCode());
        return emitterKey.startsWith(prefix);
    }

}
