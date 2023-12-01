package com.strategists.game.update.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Player;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.repository.TrendRepository;
import com.strategists.game.service.AnalysisService;
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
	private AnalysisService analysisService;

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

	protected void exportGameData() {
		analysisService.exportGameData();
	}

	protected void updateTrends() {
		analysisService.updateTrends();
	}

	protected void executePrediction(Player player) {
		analysisService.executePrediction(player);
	}

	protected void sendUpdate(T update) {
		updateService.sendUpdate(update);
	}

}
