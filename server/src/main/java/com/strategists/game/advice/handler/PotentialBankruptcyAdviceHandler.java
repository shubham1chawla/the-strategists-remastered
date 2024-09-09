package com.strategists.game.advice.handler;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import com.strategists.game.advice.AdviceContext;
import com.strategists.game.advice.AdviceType;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.Rent;
import com.strategists.game.repository.AdviceRepository;
import com.strategists.game.service.LandService;
import com.strategists.game.util.MathUtil;

import lombok.val;

@Component
@ConditionalOnExpression("${strategists.advice.enabled} && ${strategists.advice.potential-bankruptcy.enabled}")
public class PotentialBankruptcyAdviceHandler extends AbstractAdviceHandler {

	@Value("${strategists.advice.potential-bankruptcy.priority}")
	private int priority;

	@Autowired
	private LandService landService;

	@Autowired
	private AdviceRepository adviceRepository;

	@Override
	protected void generate(AdviceContext context) {
		for (Player player : context.getPlayers()) {
			if (!player.isBankrupt()) {
				generate(player, context.getLands()).ifPresent(context::addAdvice);
			}
		}
	}

	private Optional<Advice> generate(Player player, List<Land> lands) {

		// Calculating max rent to be paid in next dice size window
		val diceSize = player.getGame().getDiceSize();
		val offset = player.getIndex() + 1;

		Land maxRentLand = null;
		double maxRentAmount = 0;
		for (int i = 0; i < diceSize; i++) {
			val land = lands.get((i + offset) % lands.size());
			val totalRentAmount = MathUtil.sum(landService.getPlayerRentsByLand(player, land), Rent::getRentAmount);
			if (totalRentAmount > maxRentAmount) {
				maxRentAmount = totalRentAmount;
				maxRentLand = land;
			}
		}

		// Checking if advice is needed
		val isAdviceNeeded = Objects.nonNull(maxRentLand) && maxRentAmount >= player.getCash();

		// Checking if we have already generated advice for this player
		val opt = adviceRepository.findByPlayerAndType(player, AdviceType.POTENTIAL_BANKRUPTCY);

		// Case 1 - No previous advice found and no new advice needed
		if (opt.isEmpty() && !isAdviceNeeded) {
			return Optional.empty();
		}

		// Case 2 - No previous advice found and new advice needed
		if (opt.isEmpty() && isAdviceNeeded) {
			return Optional.of(Advice.ofPotentialBankruptcy(priority, player, maxRentAmount, maxRentLand));
		}

		// Case 3 - Previous advice's state not NEW and advice needed
		val advice = opt.get();
		if (isAdviceNeeded && (!Advice.State.NEW.equals(advice.getState())
				|| !(Objects.equals(String.valueOf(maxRentAmount), advice.getVal1())
						&& Objects.equals(maxRentLand.getName(), advice.getVal2())))) {
			advice.setState(Advice.State.NEW);
			advice.setNewCount(advice.getNewCount() + 1);
			advice.setVal1(String.valueOf(maxRentAmount));
			advice.setVal2(maxRentLand.getName());
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

}
