package com.strategists.game.advice.handler;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.ToIntFunction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import com.strategists.game.advice.AdviceContext;
import com.strategists.game.advice.AdviceType;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.repository.AdviceRepository;
import com.strategists.game.update.UpdateType;

import lombok.val;

@Component
@ConditionalOnExpression("${strategists.configuration.skip-player.enabled} && ${strategists.advice.enabled} && ${strategists.advice.avoid-timeout.enabled}")
public class AvoidTimeoutAdviceHandler extends AbstractAdviceHandler {

	@Value("${strategists.advice.avoid-timeout.priority}")
	private int priority;

	@Autowired
	private AdviceRepository adviceRepository;

	@Override
	protected void generate(AdviceContext context) {
		val game = context.getGame();
		val players = context.getPlayers();
		val getLastSkippedTurnByPlayer = getLastSkippedTurnByPlayerFunction(context);

		for (Player player : players) {
			if (!player.isBankrupt()) {
				val lastSkippedTurn = getLastSkippedTurnByPlayer.applyAsInt(player);
				generate(game, players.size(), player, lastSkippedTurn).ifPresent(context::addAdvice);
			}
		}
	}

	private Optional<Advice> generate(Game game, int playersCount, Player player, int lastSkippedTurn) {

		// Checking if advice is needed
		boolean isAdviceNeeded = false;
		if (lastSkippedTurn > 0) {
			isAdviceNeeded = (game.getTurn() - lastSkippedTurn) <= playersCount;
		}

		// Checking if we have already generated advice for this player
		val opt = adviceRepository.findByPlayerAndType(player, AdviceType.AVOID_TIMEOUT);

		// Case 1 - No previous advice found and no new advice needed
		if (opt.isEmpty() && !isAdviceNeeded) {
			return Optional.empty();
		}

		// Case 2 - No previous advice found and new advice needed
		if (opt.isEmpty() && isAdviceNeeded) {
			return Optional.of(Advice.ofAvoidTimeout(priority, player));
		}

		// Case 3 - Previous advice's state not NEW and advice needed
		val advice = opt.get();
		if (!Advice.State.NEW.equals(advice.getState()) && isAdviceNeeded) {
			advice.setState(Advice.State.NEW);
			advice.setNewCount(advice.getNewCount() + 1);
			advice.setViewed(false);
			return Optional.of(advice);
		}

		// Case 4 - Previous advice's state not FOLLOWED and advice not needed
		if (!Advice.State.FOLLOWED.equals(advice.getState()) && !isAdviceNeeded) {
			advice.setState(Advice.State.FOLLOWED);
			advice.setFollowedCount(advice.getFollowedCount() + 1);
			advice.setViewed(false);
			return Optional.of(advice);
		}

		// Case 5 - Advice NEW and needed or Advice FOLLOWED and not needed
		return Optional.empty();
	}

	private ToIntFunction<Player> getLastSkippedTurnByPlayerFunction(AdviceContext context) {
		val players = context.getPlayers();
		val activities = context.getActivities();

		val playerSkippedTurnMap = new HashMap<Player, Integer>();

		int i = 0;
		while (i < activities.size() && playerSkippedTurnMap.size() < players.size()) {
			val activity = activities.get(i);
			if (UpdateType.SKIP.equals(activity.getType())) {
				val player = context.getPlayerByUsername(activity.getVal1());
				playerSkippedTurnMap.computeIfAbsent(player, key -> activity.getTurn());
			}
			i++;
		}

		return p -> playerSkippedTurnMap.getOrDefault(p, 0);
	}

}
