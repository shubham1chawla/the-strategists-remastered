package com.strategists.game.service;

import java.util.function.Predicate;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.strategists.game.update.AbstractUpdatePayload;

public interface UpdateService {

	SseEmitter registerEmitter(String username);

	void sendUpdate(AbstractUpdatePayload<?> payload);

	void sendUpdate(AbstractUpdatePayload<?> payload, Predicate<String> filter);

}
