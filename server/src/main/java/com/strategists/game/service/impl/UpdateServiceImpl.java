package com.strategists.game.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.strategists.game.service.UpdateService;
import com.strategists.game.update.payload.UpdatePayload;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class UpdateServiceImpl implements UpdateService {

	private static final Predicate<String> ALL = username -> true;

	private Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

	@Override
	public SseEmitter registerEmitter(String username) {
		emitters.computeIfAbsent(username, key -> {
			val emitter = new SseEmitter(-1L);
			emitter.onCompletion(() -> emitters.remove(key));
			emitter.onError(ex -> emitters.remove(key));
			return emitter;
		});
		return emitters.get(username);
	}

	@Override
	public void sendUpdate(UpdatePayload<?> payload) {
		sendUpdate(payload, ALL);
	}

	@Override
	public void sendUpdate(UpdatePayload<?> payload, Predicate<String> filter) {
		emitters.entrySet().stream().filter(entry -> filter.test(entry.getKey())).forEach(entry -> {
			try {
				entry.getValue().send(payload, MediaType.APPLICATION_JSON);
			} catch (IOException ex) {
				log.debug(ex.getMessage(), ex);
			}
		});
	}

}
