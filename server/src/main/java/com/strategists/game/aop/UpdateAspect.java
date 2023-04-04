package com.strategists.game.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.strategists.game.entity.Player;
import com.strategists.game.service.UpdateService;
import com.strategists.game.update.JoinPlayerUpdatePayload;
import com.strategists.game.update.KickPlayerUpdatePayload;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Aspect
@Component
public class UpdateAspect {

	@Autowired
	private UpdateService updateService;

	@AfterReturning(value = "execution(* com.strategists.game.service.impl.PlayerServiceImpl.addPlayer(String, double))", returning = "player")
	public void postJoinPlayerAdvice(JoinPoint joinPoint, Player player) {
		log.info("Sending Join Player update to everyone.");
		updateService.sendUpdate(new JoinPlayerUpdatePayload(player));
	}

	@After("execution(* com.strategists.game.service.impl.PlayerServiceImpl.kickPlayer(String))")
	public void postKickPlayerAdvice(JoinPoint joinPoint) {
		log.info("Sending Kick Player update to everyone.");
		updateService.sendUpdate(new KickPlayerUpdatePayload((String) joinPoint.getArgs()[0]));
	}

}
