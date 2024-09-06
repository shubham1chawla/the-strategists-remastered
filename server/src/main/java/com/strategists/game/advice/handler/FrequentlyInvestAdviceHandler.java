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
@ConditionalOnExpression("${strategists.advice.enabled} && ${strategists.advice.frequently-invest.enabled}")
public class FrequentlyInvestAdviceHandler extends AbstractAdviceHandler {

	@Value("${strategists.advice.frequently-invest.priority}")
	private int priority;

	@Value("${strategists.advice.frequently-invest.turn-look-back}")
	private int turnLookBack = 3;

	@Autowired
	private AdviceRepository adviceRepository;

	@Override
	protected void generate(AdviceContext context) {
		val game = context.getGame();
		val players = context.getPlayers();
		val getLastInvestTurnByPlayer = getLastInvestTurnByPlayerFunction(context);

		// Generating or updating advice
		for (Player player : players) {
			val opt = generate(game, players.size(), player, getLastInvestTurnByPlayer.applyAsInt(player));
			if (opt.isPresent()) {
				context.addAdvice(opt.get());
			}
		}
	}

	private Optional<Advice> generate(Game game, int playersCount, Player player, int lastInvestTurn) {

		// Ignoring bankrupt players
		if (player.isBankrupt()) {
			return Optional.empty();
		}

		// Checking if advice is needed
		val isAdviceNeeded = (game.getTurn() - lastInvestTurn) > (playersCount * turnLookBack);

		// Checking if we have already generated advice for this player
		val opt = adviceRepository.findByPlayerAndType(player, AdviceType.FREQUENTLY_INVEST);

		// Case 1 - No previous advice found and no new advice needed
		if (opt.isEmpty() && !isAdviceNeeded) {
			return Optional.empty();
		}

		// Case 2 - No previous advice found and new advice needed
		if (opt.isEmpty() && isAdviceNeeded) {
			return Optional.of(Advice.ofFrequentlyInvest(priority, player, turnLookBack));
		}

		// Case 3 - Previous advice's state not NEW and advice needed
		val advice = opt.get();
		if (!Advice.State.NEW.equals(advice.getState()) && isAdviceNeeded) {
			advice.setState(Advice.State.NEW);
			advice.setNewCount(advice.getNewCount() + 1);
			return Optional.of(advice);
		}

		// Case 4 - Previous advice's state not FOLLOWED and advice not needed
		if (!Advice.State.FOLLOWED.equals(advice.getState()) && !isAdviceNeeded) {
			advice.setState(Advice.State.FOLLOWED);
			advice.setFollowedCount(advice.getFollowedCount() + 1);
			return Optional.of(advice);
		}

		// Case 5 - Advice NEW and needed or Advice FOLLOWED and not needed
		return Optional.empty();
	}

	private ToIntFunction<Player> getLastInvestTurnByPlayerFunction(AdviceContext context) {
		val players = context.getPlayers();
		val activities = context.getActivities();

		val playerInvestTurnMap = new HashMap<Player, Integer>();

		int i = 0;
		while (i < activities.size() && playerInvestTurnMap.size() < players.size()) {
			val activity = activities.get(i);
			if (UpdateType.INVEST.equals(activity.getType())) {
				val player = context.getPlayerByUsername(activity.getVal1());
				playerInvestTurnMap.computeIfAbsent(player, key -> activity.getTurn());
			}
			i++;
		}

		return p -> playerInvestTurnMap.getOrDefault(p, 0);
	}

}
