package com.strategists.game.update.handler;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.repository.TrendRepository;
import com.strategists.game.service.PredictionService;
import com.strategists.game.service.UpdateService;
import com.strategists.game.update.payload.UpdatePayload;

public abstract class AbstractUpdateHandler<T extends UpdatePayload<?>> implements UpdateHandler {

	@Value("${strategists.admin.username}")
	private String adminUsername;

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private TrendRepository trendRepository;

	@Autowired
	private PredictionService predictionService;

	@Autowired
	private UpdateService updateService;

	protected String getAdminUsername() {
		return adminUsername;
	}

	protected Activity saveActivity(Activity activity) {
		return activityRepository.save(activity);
	}

	protected void reset() {
		activityRepository.deleteAll();
		trendRepository.deleteAll();
	}

	protected void trainPredictionModelAsync(boolean export) {
		CompletableFuture.runAsync(() -> predictionService.trainPredictionModel(export));
	}

	protected void executePredictionModelAsync(Player player) {
		CompletableFuture.runAsync(() -> predictionService.executePredictionModel(player));
	}

	protected void sendUpdate(T update) {
		updateService.sendUpdate(update);
	}

}
