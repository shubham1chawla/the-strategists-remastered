package com.strategists.game.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.service.EventService;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;
import com.strategists.game.service.UpdateService;
import com.strategists.game.update.NewActivityUpdatePayload;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Aspect
@Component
public class ActivityAspect {

	private static final String ADMIN_NAME = "Creator";

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private EventService eventService;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private LandService landService;

	@Autowired
	private UpdateService updateService;

	@Around("@annotation(mapping)")
	public void postActivityAdvice(ProceedingJoinPoint joinPoint, ActivityMapping mapping) throws Throwable {
		try {
			joinPoint.proceed();
		} catch (Throwable ex) {
			log.error("Unable to log activity of type: {}", mapping.value(), ex);
			throw ex;
		}
		log.info("Logging activity of type: {}", mapping.value());
		Activity activity = null;
		switch (mapping.value()) {
		case BUY:
			activity = createBuyActivity(joinPoint.getArgs());
			break;
		case EVENT:
			activity = createEventActivity(joinPoint.getArgs());
			break;
		case JOIN:
			activity = createJoinActivity(joinPoint.getArgs());
			break;
		case KICK:
			activity = createKickActivity(joinPoint.getArgs());
			break;
		case START:
			activity = createStartActivity();
			break;
		default:
			log.warn("Unsupported Activity Type: {}", mapping.value());
			return;
		}
		updateService.sendUpdate(new NewActivityUpdatePayload(activityRepository.save(activity)));
	}

	private Activity createBuyActivity(Object[] args) {
		val p = playerService.getCurrentPlayer();
		val l = landService.getLandByIndex(p.getIndex());
		val pl = p.getPlayerLands().get(p.getPlayerLands().size() - 1);
		return Activity.ofBuy(p.getUsername(), (double) args[0], l.getName(), pl.getBuyAmount());
	}

	private Activity createEventActivity(Object[] args) {
		val l = landService.getLandById((long) args[0]);
		val e = eventService.getEventById((long) args[1]);
		return Activity.ofEvent(ADMIN_NAME, e.getName(), l.getName(), (int) args[2]);
	}

	private Activity createJoinActivity(Object[] args) {
		return Activity.ofJoin((String) args[0], (double) args[1]);
	}

	private Activity createKickActivity(Object[] args) {
		return Activity.ofKick(ADMIN_NAME, (String) args[0]);
	}

	private Activity createStartActivity() {
		return Activity.ofStart(ADMIN_NAME);
	}

}
