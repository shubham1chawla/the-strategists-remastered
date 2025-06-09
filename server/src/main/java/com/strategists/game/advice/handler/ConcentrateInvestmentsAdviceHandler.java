package com.strategists.game.advice.handler;

import com.strategists.game.advice.AdviceContext;
import com.strategists.game.advice.AdviceType;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Land;
import com.strategists.game.entity.Player;
import com.strategists.game.entity.PlayerLand;
import com.strategists.game.repository.AdviceRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@ConditionalOnExpression("${strategists.advice.enabled} && ${strategists.advice.concentrate-investments.enabled}")
public class ConcentrateInvestmentsAdviceHandler extends AbstractAdviceHandler {

    @Value("${strategists.advice.concentrate-investments.priority}")
    private int priority;

    @Value("${strategists.advice.concentrate-investments.min-investments-count}")
    private int minInvestmentsCount;

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

        // Checking if player made at least minimum required investments
        val playerLands = Optional.ofNullable(player.getPlayerLands()).orElseGet(() -> new ArrayList<PlayerLand>(0));
        if (playerLands.size() < minInvestmentsCount) {
            return Optional.empty();
        }

        // Checking if advice is needed
        val isAdviceNeeded = getMaxInvestmentsCount(player, lands) < minInvestmentsCount;

        // Checking if we have already generated advice for this player
        val opt = adviceRepository.findByPlayerAndType(player, AdviceType.CONCENTRATE_INVESTMENTS);

        // Case 1 - No previous advice found and no new advice needed
        if (opt.isEmpty() && !isAdviceNeeded) {
            return Optional.empty();
        }

        // Case 2 - No previous advice found and new advice needed
        if (opt.isEmpty()) {
            return Optional.of(Advice.ofConcentrateInvestments(priority, player, minInvestmentsCount));
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

    private int getMaxInvestmentsCount(Player player, List<Land> lands) {

        // Creating set to access player's invested lands
        val set = player.getPlayerLands().stream().map(PlayerLand::getLand).collect(Collectors.toSet());

        // Building investments count per dice size window
        val diceSize = player.getGame().getDiceSize();

        int i = 0;
        int j = 0;
        int count = 0;
        int maxCount = 0;

        while (i < lands.size()) {
            while (j - i < diceSize) {
                val land = lands.get(j % lands.size());
                if (set.contains(land)) {
                    count++;
                }
                j++;
            }
            maxCount = Math.max(maxCount, count);
            if (set.contains(lands.get(i))) {
                count--;
            }
            i++;
        }

        return maxCount;
    }

}
