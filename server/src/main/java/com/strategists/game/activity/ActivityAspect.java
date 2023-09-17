package com.strategists.game.activity;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.strategists.game.activity.handler.ActivityHandler;
import com.strategists.game.entity.Activity.Type;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.service.UpdateService;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Aspect
@Component
public class ActivityAspect {

	private Map<Type, ActivityHandler<?>> handlers;

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private UpdateService updateService;

	@Around("@annotation(mapping)")
	public Object advice(ProceedingJoinPoint joinPoint, ActivityMapping mapping) throws Throwable {
		Object obj = null;
		try {
			obj = joinPoint.proceed();
		} catch (Throwable ex) {
			log.error("Unable to log & update activity of type: {}", mapping.value(), ex);
			throw ex;
		}

		// Checking if handler is present for the activity
		if (!handlers.containsKey(mapping.value())) {
			log.warn("No handler registered for type: {}", mapping.value());
			return obj;
		}

		val handler = handlers.get(mapping.value());
		val optional = handler.apply(obj, joinPoint.getArgs());

		// Checking if optional is present
		if (optional.isEmpty()) {
			return obj;
		}
		val payload = optional.get();

		if (handler.shouldPersistActivity()) {
			activityRepository.save(payload.getActivity());
		}
		updateService.sendUpdate(payload);

		log.info("Handled activity of type: {}", mapping.value());
		return obj;
	}

	@Autowired
	public void setHandlers(List<ActivityHandler<?>> handlers) {
		this.handlers = handlers.stream().collect(Collectors.toMap(ActivityHandler::getType, Function.identity()));
	}

}
