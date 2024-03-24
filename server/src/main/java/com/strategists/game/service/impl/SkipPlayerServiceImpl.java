package com.strategists.game.service.impl;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.strategists.game.entity.Game;
import com.strategists.game.service.GameService;
import com.strategists.game.service.PlayerService;
import com.strategists.game.service.SkipPlayerService;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@ConditionalOnProperty(name = "strategists.configuration.skip-player.enabled", havingValue = "true")
public class SkipPlayerServiceImpl extends AbstractSchedulerService implements SkipPlayerService {

	private static final String SKIP_PLAYER_SCHEDULER = "Skipper";

	@Value("${strategists.configuration.skip-player.thread-pool-size}")
	private int threadPoolSize;

	@Autowired
	private GameService gameService;

	@Autowired
	private PlayerService playerService;

	@PostConstruct
	public void setup() {
		initialize(SKIP_PLAYER_SCHEDULER, threadPoolSize);
		log.info("Players skipping service enabled.");
	}

	@Override
	public void schedule(Game game) {
		super.schedule(game);
		log.info("Scheduled skip-player task for game: {}", game.getCode());
	}

	@Override
	public void unschedule(Game game) {
		super.unschedule(game);
		log.info("Unscheduled skip-player task for game: {}", game.getCode());
	}

	@Override
	protected void validate(Game game) throws IllegalArgumentException {
		Assert.notNull(game.getSkipPlayerTimeout(), "Skip player timeout not set for game: " + game.getCode());
	}

	@Override
	protected Date getScheduleDate(Game game) {
		return new Date(System.currentTimeMillis() + game.getSkipPlayerTimeout());
	}

	@Override
	protected void execute(Game game) {

		// Fetching the current player
		val player = playerService.getCurrentPlayer(game);
		log.info("Skipping {}'s turn in game: {}", player.getUsername(), player.getGameCode());

		// Declaring the player bankrupt if player skipped more than a few times
		playerService.skipPlayer(player);
		if (player.getRemainingSkipsCount() <= 0) {
			playerService.bankruptPlayer(player);
		}

		// Playing next turn
		gameService.playTurn(game);
	}

}
