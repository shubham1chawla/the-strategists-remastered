package com.strategists.game.aop;

import java.util.List;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;
import com.strategists.game.service.UpdateService;
import com.strategists.game.update.AbstractUpdatePayload;
import com.strategists.game.update.InvestmentUpdatePayload;
import com.strategists.game.update.JoinPlayerUpdatePayload;
import com.strategists.game.update.KickPlayerUpdatePayload;
import com.strategists.game.update.StartUpdatePayload;
import com.strategists.game.update.TurnUpdatePayload;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Aspect
@Component
public class UpdateAspect {

	@Autowired
	private PlayerService playerService;

	@Autowired
	private LandService landService;

	@Autowired
	private UpdateService updateService;

	@Around("@annotation(mapping)")
	public Object advice(ProceedingJoinPoint joinPoint, UpdateMapping mapping) throws Throwable {
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
		case INVEST:
			payload = createInvestmentUpdatePayload(joinPoint.getArgs());
			break;
		case JOIN:
			payload = new JoinPlayerUpdatePayload((Player) obj);
			break;
		case KICK:
			payload = new KickPlayerUpdatePayload((String) joinPoint.getArgs()[0]);
			break;
		case START:
			payload = new StartUpdatePayload(playerService.getCurrentPlayer());
			break;
		case TURN:
			payload = createTurnUpdatePayload(obj);
			break;
		default:
			log.warn("Unsupported Activity Type: {}", mapping.value());
			return obj;
		}
		updateService.sendUpdate(payload);
		return obj;
	}

	private InvestmentUpdatePayload createInvestmentUpdatePayload(Object... args) {
		val land = landService.getLandById((long) args[1]);

		/*
		 * Updating all the players that are linked with this land. Each player's
		 * net-worth is tied with the market value of the land, therefore investment in
		 * this land will boost each investors' net-worth.
		 */
		val players = land.getPlayerLands().stream().map(PlayerLand::getPlayer).collect(Collectors.toList());

		return new InvestmentUpdatePayload(land, players);
	}

	private TurnUpdatePayload createTurnUpdatePayload(Object obj) {
		val players = (List<?>) obj;
		return new TurnUpdatePayload((Player) players.get(0), (Player) players.get(1));
	}

}
