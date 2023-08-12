package com.strategists.game.aop;

import java.util.HashSet;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.entity.Rent;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;
import com.strategists.game.service.UpdateService;
import com.strategists.game.update.AbstractUpdatePayload;
import com.strategists.game.update.BankruptcyUpdatePayload;
import com.strategists.game.update.InvestmentUpdatePayload;
import com.strategists.game.update.JoinPlayerUpdatePayload;
import com.strategists.game.update.KickPlayerUpdatePayload;
import com.strategists.game.update.MoveUpdatePayload;
import com.strategists.game.update.RentUpdatePayload;
import com.strategists.game.update.StartUpdatePayload;
import com.strategists.game.update.TurnUpdatePayload;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Aspect
@Component
public class ActivityAspect {

	@Value("${strategists.admin.username}")
	private String adminUsername;

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private PlayerService playerService;

	@Autowired
	private LandService landService;

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
		log.info("Logging & updating activity of type: {}", mapping.value());
		AbstractUpdatePayload<?> payload = null;
		switch (mapping.value()) {
		case BANKRUPT:
			payload = handleBankruptActivity(joinPoint.getArgs());
			break;
		case INVEST:
			payload = handleInvestActivity(joinPoint.getArgs());
			break;
		case JOIN:
			payload = handleJoinPlayerActivity(obj);
			break;
		case KICK:
			payload = handleKickPlayerActivity(joinPoint.getArgs());
			break;
		case MOVE:
			payload = handleMoveActivity(obj);
			break;
		case RENT:
			payload = handleRentActivity(joinPoint.getArgs());
			break;
		case START:
			payload = handleStartActivity();
			break;
		case TURN:
			payload = handleTurnActivity(obj);
			break;
		default:
			log.warn("Unsupported Activity Type: {}", mapping.value());
			return obj;
		}
		updateService.sendUpdate(payload);
		return obj;
	}

	private BankruptcyUpdatePayload handleBankruptActivity(Object... args) {
		val player = (Player) args[0];

		val players = new HashSet<Player>();
		val lands = player.getPlayerLands().stream().map(PlayerLand::getLand).toList();
		for (Land land : lands) {
			players.addAll(land.getPlayerLands().stream().map(PlayerLand::getPlayer).toList());
		}

		// Creating activity for bankruptcy
		val activity = activityRepository.save(Activity.ofBankrupt(player.getUsername()));

		return new BankruptcyUpdatePayload(activity, lands, players);
	}

	private InvestmentUpdatePayload handleInvestActivity(Object... args) {
		val curr = playerService.getCurrentPlayer();
		val land = landService.getLandByIndex(curr.getIndex());

		/*
		 * Updating all the players that are linked with this land. Each player's
		 * net-worth is tied with the market value of the land, therefore investment in
		 * this land will boost each investors' net-worth.
		 */
		val players = land.getPlayerLands().stream().map(PlayerLand::getPlayer).toList();

		// Creating activity for investment
		val activity = activityRepository.save(Activity.ofInvest(curr.getUsername(), (double) args[2], land.getName()));

		return new InvestmentUpdatePayload(activity, land, players);
	}

	private JoinPlayerUpdatePayload handleJoinPlayerActivity(Object obj) {
		val player = (Player) obj;

		// Creating activity for join
		val activity = activityRepository.save(Activity.ofJoin(player.getUsername(), player.getCash()));

		return new JoinPlayerUpdatePayload(activity, player);
	}

	private KickPlayerUpdatePayload handleKickPlayerActivity(Object[] args) {
		val activity = activityRepository.save(Activity.ofKick(adminUsername, (String) args[0]));
		return new KickPlayerUpdatePayload(activity, (String) args[0]);
	}

	private MoveUpdatePayload handleMoveActivity(Object obj) {
		val player = (Player) obj;
		val land = landService.getLandByIndex(player.getIndex());

		// Creating activity for move
		val activity = activityRepository.save(Activity.ofMove(player.getUsername(), land.getName()));

		return new MoveUpdatePayload(activity, player);
	}

	private RentUpdatePayload handleRentActivity(Object[] args) {
		val rent = (Rent) args[0];
		val sourcePlayer = rent.getSourcePlayer();
		val targetPlayer = rent.getTargetPlayer();
		val land = rent.getLand();
		val rentAmount = rent.getRentAmount();

		// Creating activity for rent
		val activity = activityRepository.save(
				Activity.ofRent(sourcePlayer.getUsername(), rentAmount, targetPlayer.getUsername(), land.getName()));

		return new RentUpdatePayload(activity, List.of(sourcePlayer, targetPlayer));
	}

	private StartUpdatePayload handleStartActivity() {
		val activity = activityRepository.save(Activity.ofStart(adminUsername));
		return new StartUpdatePayload(activity, playerService.getCurrentPlayer());
	}

	private TurnUpdatePayload handleTurnActivity(Object obj) {
		val players = (List<?>) obj;
		val curr = (Player) players.get(0);
		val prev = (Player) players.get(1);

		// Creating activity for turn
		val activity = activityRepository.save(Activity.ofTurn(prev.getUsername(), curr.getUsername()));

		return new TurnUpdatePayload(activity, curr, prev);
	}

}
