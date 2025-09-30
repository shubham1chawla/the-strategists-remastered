package com.strategists.game.advice.handler;

import com.strategists.game.advice.AdviceContext;
import com.strategists.game.advice.AdviceType;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.repository.AdviceRepository;
import com.strategists.game.update.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.ToIntFunction;

@Component
@ConditionalOnExpression("${strategists.advices.enabled} && ${strategists.advices.frequently-invest.enabled}")
public class FrequentlyInvestAdviceHandler extends AbstractAdviceHandler {

    @Value("${strategists.advices.frequently-invest.priority}")
    private int priority;

    @Value("${strategists.advices.frequently-invest.turn-look-back}")
    private int turnLookBack;

    @Autowired
    private AdviceRepository adviceRepository;

    @Override
    protected void generate(AdviceContext context) {
        final var game = context.getGame();
        final var players = context.getPlayers();
        final var getLastInvestTurnByPlayer = getLastInvestTurnByPlayerFunction(context);

        for (Player player : players) {
            if (!player.isBankrupt()) {
                final var lastInvestTurn = getLastInvestTurnByPlayer.applyAsInt(player);
                generate(game, players.size(), player, lastInvestTurn).ifPresent(context::addAdvice);
            }
        }
    }

    private Optional<Advice> generate(Game game, int playersCount, Player player, int lastInvestTurn) {

        // Checking if advice is needed
        final var isAdviceNeeded = (game.getTurn() - lastInvestTurn) > (playersCount * turnLookBack);

        // Checking if we have already generated advice for this player
        final var opt = adviceRepository.findByPlayerAndType(player, AdviceType.FREQUENTLY_INVEST);

        // Case 1 - No previous advice found and no new advice needed
        if (opt.isEmpty() && !isAdviceNeeded) {
            return Optional.empty();
        }

        // Case 2 - No previous advice found and new advice needed
        if (opt.isEmpty()) {
            return Optional.of(Advice.ofFrequentlyInvest(priority, player, turnLookBack));
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

    private ToIntFunction<Player> getLastInvestTurnByPlayerFunction(AdviceContext context) {
        final var players = context.getPlayers();
        final var activities = context.getActivities();

        final var playerInvestTurnMap = new HashMap<Player, Integer>();

        var i = 0;
        while (i < activities.size() && playerInvestTurnMap.size() < players.size()) {
            final var activity = activities.get(i);
            if (UpdateType.INVEST.equals(activity.getType())) {
                final var player = context.getPlayerByUsername(activity.getVal1());
                playerInvestTurnMap.computeIfAbsent(player, key -> activity.getTurn());
            }
            i++;
        }

        return p -> playerInvestTurnMap.getOrDefault(p, 0);
    }

}
