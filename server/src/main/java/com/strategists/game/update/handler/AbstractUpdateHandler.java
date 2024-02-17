package com.strategists.game.update.handler;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Game;
import com.strategists.game.entity.Player;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.repository.TrendRepository;
import com.strategists.game.service.PredictionService;
import com.strategists.game.service.UpdateService;
import com.strategists.game.update.payload.UpdatePayload;

public abstract class AbstractUpdateHandler<T extends UpdatePayload<?>> implements UpdateHandler {

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private TrendRepository trendRepository;

	@Autowired
	private PredictionService predictionService;

	@Autowired
	private UpdateService updateService;

	protected Activity saveActivity(Activity activity) {
		return activityRepository.save(activity);
	}

	protected void reset(Game game) {
		activityRepository.deleteByGame(game);
		trendRepository.deleteByGame(game);
	}

	protected void trainPredictionModelAsync(Game game) {
		CompletableFuture.runAsync(() -> predictionService.trainPredictionModel(game));
	}

	protected void executePredictionModelAsync(Player player) {
		CompletableFuture.runAsync(() -> predictionService.executePredictionModel(player));
	}

	protected void sendUpdate(Game game, T update) {
		updateService.sendUpdate(game, update);
	}

}
