package com.strategists.game.update.handler;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Game;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.repository.TrendRepository;
import com.strategists.game.service.AdviceService;
import com.strategists.game.service.CleanUpService;
import com.strategists.game.service.PredictionService;
import com.strategists.game.service.SkipPlayerService;
import com.strategists.game.service.UpdateService;
import com.strategists.game.update.payload.UpdatePayload;

import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class AbstractUpdateHandler<T extends UpdatePayload<?>> implements UpdateHandler {

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private TrendRepository trendRepository;

	@Autowired
	private UpdateService updateService;

	@Autowired(required = false)
	private PredictionService predictionService;

	@Autowired(required = false)
	private AdviceService adviceService;

	@Autowired(required = false)
	private SkipPlayerService skipPlayerService;

	@Autowired(required = false)
	private CleanUpService cleanUpService;

	protected Activity saveActivity(Activity activity) {
		return activityRepository.saveAndFlush(activity);
	}

	protected void reset(Game game) {
		activityRepository.deleteByGame(game);
		trendRepository.deleteByGame(game);
		if (Objects.nonNull(predictionService)) {
			predictionService.clearPredictions(game);
		}
		if (Objects.nonNull(adviceService)) {
			adviceService.clearAdvices(game);
		}
	}

	protected void trainPredictionModelAsync(Game game) {
		if (Objects.nonNull(predictionService)) {
			CompletableFuture.runAsync(() -> {
				try {
					predictionService.trainPredictionModel(game);
				} catch (Exception ex) {
					log.error("Failed to train prediction model!", ex);
				}
			});
		}
	}

	protected void executePredictionModelAsync(Game game) {
		if (Objects.nonNull(predictionService)) {
			CompletableFuture.runAsync(() -> {
				try {
					predictionService.executePredictionModel(game);
				} catch (Exception ex) {
					log.error("Failed to execute prediction model!", ex);
				}
			});
		}
	}

	protected void generateAdvicesAsync(Game game) {
		if (Objects.nonNull(adviceService)) {
			CompletableFuture.runAsync(() -> {
				try {
					adviceService.generateAdvices(game);
				} catch (Exception ex) {
					log.error("Failed to generate advices!", ex);
				}
			});
		}
	}

	protected void exportAdvicesAsync(Game game) {
		if (Objects.nonNull(adviceService)) {
			CompletableFuture.runAsync(() -> {
				try {
					adviceService.exportAdvices(game);
				} catch (Exception ex) {
					log.error("Failed to export advices!", ex);
				}
			});
		}
	}

	protected void scheduleSkipPlayerTask(Game game) {
		if (Objects.nonNull(skipPlayerService)) {
			skipPlayerService.schedule(game);
		}
	}

	protected void unscheduleSkipPlayerTask(Game game) {
		if (Objects.nonNull(skipPlayerService)) {
			skipPlayerService.unschedule(game);
		}
	}

	protected void scheduleCleanUpTask(Game game) {
		if (Objects.nonNull(cleanUpService)) {
			cleanUpService.schedule(game);
		}
	}

	protected void unscheduleCleanUpTask(Game game) {
		if (Objects.nonNull(cleanUpService)) {
			cleanUpService.unschedule(game);
		}
	}

	protected void sendUpdate(Game game, T update) {
		updateService.sendUpdate(game, update);
	}

	protected void closeEmitters(Game game) {
		updateService.closeEmitters(game);
	}

}
