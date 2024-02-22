package com.strategists.game.service.impl;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.strategists.game.entity.Game;
import com.strategists.game.service.GameService;
import com.strategists.game.service.PlayerService;
import com.strategists.game.service.SkipPlayerService;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@ConditionalOnProperty(name = "strategists.configuration.skip-player.enabled", havingValue = "true")
public class SkipPlayerServiceImpl implements SkipPlayerService {

	private static final String SKIP_PLAYER_SCHEDULER = "SkipScheduler";

	/**
	 * The allowed emails provide an insight on how many games can run
	 * simultaneously. We just need 1 thread per game to skip players.
	 */
	@Value("#{'${strategists.security.allowed-emails}'.split(',').length}")
	private int threadPoolSize;

	@Value("${strategists.configuration.skip-player.timeout.value}")
	private int timeoutValue;

	@Value("${strategists.configuration.skip-player.timeout.unit}")
	private TimeUnit timeoutUnit;

	@Autowired
	private TransactionTemplate template;

	@Autowired
	private GameService gameService;

	@Autowired
	private PlayerService playerService;

	private ThreadPoolTaskScheduler scheduler;
	private Map<Long, ScheduledFuture<?>> futures;

	@PostConstruct
	public void setup() {
		log.info("Skipping players every {} seconds.", timeoutUnit.toSeconds(timeoutValue));

		// Setting up scheduler
		scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(threadPoolSize);
		scheduler.setThreadNamePrefix(SKIP_PLAYER_SCHEDULER);
		scheduler.initialize();

		// Setting up futures collection
		futures = new ConcurrentHashMap<>(threadPoolSize);
	}

	@Override
	public void schedule(Game game) {

		/**
		 * Canceling the executing of the thread here is justifiable even if there are
		 * database-related calls post this point. All such calls are done on a separate
		 * thread scheduled below.
		 * 
		 * Ideally, this cleanup here just ensures that previously set futures aren't
		 * called at all. Therefore, no need to worry about the database calls that the
		 * canceled thread makes.
		 */
		unschedule(game);

		val date = new Date(System.currentTimeMillis() + timeoutUnit.toMillis(timeoutValue));
		val future = scheduler.schedule(() -> {

			// Setting up thread name
			Thread.currentThread().setName(String.format("%s-%s", SKIP_PLAYER_SCHEDULER, game.getId()));

			// Checking if future was cancelled
			if (Thread.currentThread().isInterrupted()) {
				return;
			}

			/**
			 * Running the database-related actions inside a transaction template to set
			 * Hibernate session inside this thread execution.
			 */
			template.executeWithoutResult(status -> {

				// Fetching the current player
				val player = playerService.getCurrentPlayer(game);
				log.info("Skipping {}'s turn in game ID: {}", player.getUsername(), player.getGameId());

				// Declaring the player bankrupt if player skipped more than a few times
				playerService.skipPlayer(player);
				if (player.getRemainingSkipsCount() <= 0) {
					playerService.bankruptPlayer(player);
				}

				// Playing next turn
				gameService.playTurn(game);

			});

		}, date);
		futures.put(game.getId(), future);

		log.info("Scheduled skip player task for game ID: {}", game.getId());
	}

	@Override
	public void unschedule(Game game) {
		if (!futures.containsKey(game.getId())) {
			return;
		}
		val future = futures.get(game.getId());

		/**
		 * Since we are canceling the future and interrupting the thread, you shouldn't
		 * call any database-related methods post calling this method. You will see SQL
		 * exceptions.
		 */
		future.cancel(true);

		futures.remove(game.getId());
		log.info("Unscheduled previous skip player task for game ID: {}", game.getId());
	}

}
