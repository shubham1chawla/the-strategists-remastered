package com.strategists.game.advice.handler;

import com.strategists.game.advice.AdviceContext;
import com.strategists.game.advice.AdviceType;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.repository.AdviceRepository;
import com.strategists.game.util.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Optional;

@Component
@ConditionalOnExpression("${strategists.advices.enabled} && ${strategists.advices.significant-investments.enabled}")
public class SignificantInvestmentsAdviceHandler extends AbstractAdviceHandler {

    @Value("${strategists.advices.significant-investments.priority}")
    private int priority;

    @Value("${strategists.advices.significant-investments.min-average-ownership}")
    private double minAverageOwnership;

    @Autowired
    private AdviceRepository adviceRepository;

    @Override
    protected void generate(AdviceContext context) {
        for (Player player : context.getPlayers()) {
            if (!player.isBankrupt()) {
                generate(player).ifPresent(context::addAdvice);
            }
        }
    }

    private Optional<Advice> generate(Player player) {

        // Checking if player made any investments
        if (CollectionUtils.isEmpty(player.getPlayerLands())) {
            return Optional.empty();
        }

        // Calculating average ownership
        final var investmentsCount = player.getPlayerLands().size();
        final var totalOwnership = MathUtil.sum(player.getPlayerLands(), PlayerLand::getOwnership);
        final var averageOwnership = totalOwnership / investmentsCount;

        // Checking if advice is needed
        final var isAdviceNeeded = averageOwnership < minAverageOwnership;

        // Checking if we have already generated advice for this player
        final var opt = adviceRepository.findByPlayerAndType(player, AdviceType.SIGNIFICANT_INVESTMENTS);

        // Case 1 - No previous advice found and no new advice needed
        if (opt.isEmpty() && !isAdviceNeeded) {
            return Optional.empty();
        }

        // Case 2 - No previous advice found and new advice needed
        if (opt.isEmpty()) {
            return Optional.of(Advice.ofSignificantInvestments(priority, player, minAverageOwnership));
        }

        // Case 3 - Previous advice's state not NEW and advice needed
        final var advice = opt.get();
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

}
