package com.strategists.game.service;

import java.util.function.Predicate;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.strategists.game.update.payload.UpdatePayload;

public interface UpdateService {

	SseEmitter registerEmitter(String username);

	void sendUpdate(UpdatePayload<?> payload);

	void sendUpdate(UpdatePayload<?> payload, Predicate<String> filter);

}
