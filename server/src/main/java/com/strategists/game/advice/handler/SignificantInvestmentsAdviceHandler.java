package com.strategists.game.advice.handler;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.strategists.game.advice.AdviceContext;
import com.strategists.game.advice.AdviceType;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Player;
import com.strategists.game.repository.AdviceRepository;
import com.strategists.game.util.MathUtil;

import lombok.val;

@Component
@ConditionalOnExpression("${strategists.advice.enabled} && ${strategists.advice.significant-investments.enabled}")
public class SignificantInvestmentsAdviceHandler extends AbstractAdviceHandler {

	@Value("${strategists.advice.significant-investments.priority}")
	private int priority;

	@Value("${strategists.advice.significant-investments.min-average-ownership}")
	private double minAverageOwnership;

	@Autowired
	private AdviceRepository adviceRepository;

	@Override
	protected void generate(AdviceContext context) {
		for (Player player : context.getPlayers()) {
			val opt = generate(player);
			if (opt.isPresent()) {
				context.addAdvice(opt.get());
			}
		}
	}

	private Optional<Advice> generate(Player player) {

		// Checking if player made any investments
		if (player.isBankrupt() || CollectionUtils.isEmpty(player.getPlayerLands())) {
			return Optional.empty();
		}

		// Calculating average ownership
		val investmentsCount = player.getPlayerLands().size();
		val totalOwnership = MathUtil.sum(player.getPlayerLands(), pl -> pl.getOwnership());
		val averageOwnership = totalOwnership / investmentsCount;

		// Checking if advice is needed
		val isAdviceNeeded = averageOwnership < minAverageOwnership;

		// Checking if we have already generated advice for this player
		val opt = adviceRepository.findByPlayerAndType(player, AdviceType.SIGNIFICANT_INVESTMENTS);

		// Case 1 - No previous advice found and no new advice needed
		if (opt.isEmpty() && !isAdviceNeeded) {
			return Optional.empty();
		}

		// Case 2 - No previous advice found and new advice needed
		if (opt.isEmpty() && isAdviceNeeded) {
			return Optional.of(Advice.ofSignificantInvestments(priority, player, minAverageOwnership));
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

}
