package com.strategists.game.update.handler;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.repository.TrendRepository;
import com.strategists.game.service.PredictionService;
import com.strategists.game.service.SkipPlayerService;
import com.strategists.game.service.UpdateService;
import com.strategists.game.update.payload.UpdatePayload;

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
	private SkipPlayerService skipPlayerService;

	protected Activity saveActivity(Activity activity) {
		return activityRepository.saveAndFlush(activity);
	}

	protected void reset(Game game) {
		activityRepository.deleteByGame(game);
		trendRepository.deleteByGame(game);
	}

	protected void trainPredictionModelAsync(Game game) {
		if (Objects.nonNull(predictionService)) {
			CompletableFuture.runAsync(() -> predictionService.trainPredictionModel(game));
		}
	}

	protected void executePredictionModelAsync(Player player) {
		if (Objects.nonNull(predictionService)) {
			CompletableFuture.runAsync(() -> predictionService.executePredictionModel(player));
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

	protected void sendUpdate(Game game, T update) {
		updateService.sendUpdate(game, update);
	}

}
