package com.strategists.game.service.impl;

import com.strategists.game.entity.Game;
import com.strategists.game.service.CleanUpService;
import com.strategists.game.service.GameService;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

@Log4j2
@Service
@ConditionalOnProperty(name = "strategists.configuration.clean-up.enabled", havingValue = "true")
public class CleanUpServiceImpl extends AbstractSchedulerService implements CleanUpService {

    private static final String CLEAN_UP_SCHEDULER = "Cleaner";

    @Value("${strategists.configuration.clean-up.thread-pool-size}")
    private int threadPoolSize;

    @Autowired
    private GameService gameService;

    @PostConstruct
    public void setup() {
        initialize(CLEAN_UP_SCHEDULER, threadPoolSize);
        log.info("Game clean-up service enabled.");
    }

    @Override
    public void schedule(Game game) {
        super.schedule(game);
        log.info("Scheduled clean-up task for game: {}", game.getCode());
    }

    @Override
    public void unschedule(Game game) {
        super.unschedule(game);
        log.info("Unscheduled clean-up task for game: {}", game.getCode());
    }

    @Override
    protected void validate(Game game) throws IllegalArgumentException {
        Assert.notNull(game.getCleanUpDelay(), "Clean-up delay not set for game: " + game.getCode());
    }

    @Override
    protected Date getScheduleDate(Game game) {
        return new Date(System.currentTimeMillis() + game.getCleanUpDelay());
    }

    @Override
    protected void execute(Game game) {
        gameService.deleteGame(game);
    }

}
