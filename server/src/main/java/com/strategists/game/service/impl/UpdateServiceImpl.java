package com.strategists.game.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.strategists.game.entity.Game;
import com.strategists.game.service.PlayerService;
import com.strategists.game.service.UpdateService;
import com.strategists.game.update.payload.PingUpdatePayload;
import com.strategists.game.update.payload.UpdatePayload;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class UpdateServiceImpl implements UpdateService {

	@Autowired
	private PlayerService playerService;

	private Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

	@Override
	public SseEmitter registerEmitter(Game game, String username) {
		val emitterKey = playerService.getPlayerByUsername(game, username).getGamePlayerKey();
		emitters.computeIfAbsent(emitterKey, key -> {
			val emitter = new SseEmitter(-1L);
			emitter.onCompletion(() -> emitters.remove(key));
			emitter.onError(ex -> emitters.remove(key));
			return emitter;
		});
		return emitters.get(emitterKey);
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
				log.debug(ex.getMessage(), ex);
			}
		});
	}

	private boolean filterByGame(Game game, String emitterKey) {
		val prefix = String.format("game-%s", game.getCode());
		return emitterKey.startsWith(prefix);
	}

}
