package com.strategists.game.service.impl;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import com.strategists.game.entity.Game;
import com.strategists.game.service.GenericSchedulerService;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class AbstractSchedulerService implements GenericSchedulerService {

	@Autowired
	private TransactionTemplate template;

	private ThreadPoolTaskScheduler scheduler;
	private Map<String, ScheduledFuture<?>> futures;

	protected void initialize(String schedulerName, int threadPoolSize) {

		// Setting up scheduler
		scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(threadPoolSize);
		scheduler.setThreadNamePrefix(schedulerName);
		scheduler.initialize();

		// Setting up futures collection
		futures = new ConcurrentHashMap<>();
	}

	@Override
	public void schedule(Game game) {

		// Checking whether service is initialized
		Assert.notNull(scheduler, "Invoke 'initialize' method to setup ThreadPoolTaskScheduler!");

		// Validating whether to schedule task or not
		validate(game);

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

		val future = scheduler.schedule(() -> {

			// Setting up thread name
			Thread.currentThread().setName(String.format("%s-%s", scheduler.getThreadNamePrefix(), game.getCode()));

			// Checking if future was cancelled
			if (Thread.currentThread().isInterrupted()) {
				return;
			}

			/**
			 * Running the database-related actions inside a transaction template to set
			 * Hibernate session inside this thread execution.
			 */
			template.executeWithoutResult(status -> {
				try {
					execute(game);
				} catch (Exception ex) {
					log.error(ex);
				}
			});

		}, getScheduleDate(game));
		futures.put(game.getCode(), future);
	}

	@Override
	public void unschedule(Game game) {
		if (!futures.containsKey(game.getCode())) {
			return;
		}
		val future = futures.get(game.getCode());

		/**
		 * Since we are canceling the future and interrupting the thread, you shouldn't
		 * call any database-related methods post calling this method. You will see SQL
		 * exceptions.
		 */
		future.cancel(true);

		futures.remove(game.getCode());
	}

	protected abstract void validate(Game game) throws IllegalArgumentException;

	protected abstract Date getScheduleDate(Game game);

	protected abstract void execute(Game game);

}
