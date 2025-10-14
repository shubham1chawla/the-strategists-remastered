package com.strategists.game.service.impl;

import com.strategists.game.advice.AdviceContext;
import com.strategists.game.advice.handler.AbstractAdviceHandler;
import com.strategists.game.entity.Advice;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.repository.AdviceRepository;
import com.strategists.game.service.AdvicesService;
import com.strategists.game.service.LandService;
import com.strategists.game.service.PlayerService;
import com.strategists.game.update.UpdateMapping;
import com.strategists.game.update.UpdateType;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.chain.impl.ChainBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Log4j2
@Service
@Transactional
@ConditionalOnProperty(name = "strategists.advices.enabled", havingValue = "true")
public class AdvicesServiceImpl implements AdvicesService {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private LandService landService;

    @Autowired
    private AdviceRepository adviceRepository;

    @Autowired
    private List<AbstractAdviceHandler> handlers;

    @PostConstruct
    public void setup() {
        log.info("Advices enabled! Total handlers registered: {}", handlers.size());
    }

    @Override
    @UpdateMapping(UpdateType.ADVICE)
    public List<Advice> generateAdvices(Game game) {
        log.info("Generating advices for game: {}", game.getCode());

        // Checking if any handler is available
        if (CollectionUtils.isEmpty(handlers)) {
            log.warn("No advice handlers enabled. Skipping generating advices...");
            return List.of();
        }

        // Creating the chain from available handlers
        final var chain = new ChainBase();
        for (AbstractAdviceHandler handler : handlers) {
            chain.addCommand(handler);
        }

        // Adding information to advice context
        final var players = playerService.getPlayersByGame(game);
        final var lands = landService.getLandsByGame(game);
        final var context = new AdviceContext(game, players, lands);

        // Executing advice chain
        try {
            chain.execute(context);
        } catch (Exception ex) {
            log.error("Unable to complete advice chain! Message: {}", ex.getMessage(), ex);
            return List.of();
        }

        // Saving new and updated records
        return adviceRepository.saveAll(context.getAdvices());
    }

    @Override
    public List<Advice> getAdvicesByGame(Game game) {
        return adviceRepository.findByGameOrderByPriority(game);
    }

    @Override
    @UpdateMapping(UpdateType.ADVICE)
    public List<Advice> markPlayerAdvicesViewed(Player player) {
        log.info("Marking {}'s advices as viewed for game: {}", player.getUsername(), player.getGame().getCode());
        final var advices = adviceRepository.findByPlayerAndViewed(player, false);
        if (CollectionUtils.isEmpty(advices)) {
            return List.of();
        }
        return adviceRepository.saveAll(advices.stream().peek(advice -> advice.setViewed(true)).toList());
    }

    @Override
    public void clearAdvices(Game game) {
        adviceRepository.deleteByGame(game);
    }

}
