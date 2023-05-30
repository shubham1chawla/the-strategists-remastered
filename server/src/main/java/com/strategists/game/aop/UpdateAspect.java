package com.strategists.game.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.strategists.game.entity.Player;
import com.strategists.game.service.UpdateService;
import com.strategists.game.update.AbstractUpdatePayload;
import com.strategists.game.update.JoinPlayerUpdatePayload;
import com.strategists.game.update.KickPlayerUpdatePayload;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Aspect
@Component
public class UpdateAspect {

	@Autowired
	private UpdateService updateService;

	@Around("@annotation(mapping)")
	public void advice(ProceedingJoinPoint joinPoint, UpdateMapping mapping) throws Throwable {
		Object obj = null;
		try {
			obj = joinPoint.proceed();
		} catch (Throwable ex) {
			log.error("Unable to update activity of type: {}", mapping.value(), ex);
			throw ex;
		}
		log.info("Updating activity of type: {}", mapping.value());
		AbstractUpdatePayload<?> payload = null;
		switch (mapping.value()) {
		case JOIN:
			payload = new JoinPlayerUpdatePayload((Player) obj);
			break;
		case KICK:
			payload = new KickPlayerUpdatePayload((String) joinPoint.getArgs()[0]);
			break;
		default:
			log.warn("Unsupported Activity Type: {}", mapping.value());
			return;
		}
		updateService.sendUpdate(payload);
	}

}
