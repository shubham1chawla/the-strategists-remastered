package com.strategists.game.advice.handler;

import com.strategists.game.advice.AdviceContext;
import com.strategists.game.advice.AdviceType;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.repository.AdviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@ConditionalOnExpression("${strategists.skip-player.enabled} && ${strategists.advices.enabled} && ${strategists.advices.avoid-timeout.enabled}")
public class AvoidTimeoutAdviceHandler extends AbstractAdviceHandler {

    @Value("${strategists.advices.avoid-timeout.priority}")
    private int priority;

    @Autowired
    private AdviceRepository adviceRepository;

    @Override
    protected void generate(AdviceContext context) {
        final var game = context.getGame();
        final var players = context.getPlayers();
        final var activePlayersCount = players.stream().filter(Player::isActive).count();

        for (Player player : players) {
            if (!player.isBankrupt()) {
                generate(game, activePlayersCount, player).ifPresent(context::addAdvice);
            }
        }
    }

    private Optional<Advice> generate(Game game, long playersCount, Player player) {

        // Checking if advice is needed
        boolean isAdviceNeeded = false;
        if (Objects.nonNull(player.getLastSkippedStep())) {
            isAdviceNeeded = (game.getCurrentStep() - player.getLastSkippedStep()) <= playersCount;
        }

        // Checking if we have already generated advice for this player
        final var opt = adviceRepository.findByPlayerAndType(player, AdviceType.AVOID_TIMEOUT);

        // Case 1 - No previous advice found and no new advice needed
        if (opt.isEmpty() && !isAdviceNeeded) {
            return Optional.empty();
        }

        // Case 2 - No previous advice found and new advice needed
        if (opt.isEmpty()) {
            return Optional.of(Advice.ofAvoidTimeout(priority, player));
        }

        // Case 3 - Previous advice's state not NEW and advice needed
        final var advice = opt.get();
        if (!Advice.State.NEW.equals(advice.getState()) && isAdviceNeeded) {
            advice.setState(Advice.State.NEW);
            advice.setViewed(false);
            return Optional.of(advice);
        }

        // Case 4 - Previous advice's state not FOLLOWED and advice not needed
        if (!Advice.State.FOLLOWED.equals(advice.getState()) && !isAdviceNeeded) {
            advice.setState(Advice.State.FOLLOWED);
            advice.setViewed(false);
            return Optional.of(advice);
        }

        // Case 5 - Advice NEW and needed or Advice FOLLOWED and not needed
        return Optional.empty();
    }

}
