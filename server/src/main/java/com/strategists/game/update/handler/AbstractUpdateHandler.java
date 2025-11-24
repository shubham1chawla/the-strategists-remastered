package com.strategists.game.update.handler;

import com.strategists.game.configuration.properties.CleanUpConfigurationProperties;
import com.strategists.game.configuration.properties.SkipPlayerConfigurationProperties;
import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Game;
import com.strategists.game.listener.event.CleanUpEvent;
import com.strategists.game.listener.event.SkipPlayerEvent;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.service.HistoryService;
import com.strategists.game.service.SchedulerService;
import com.strategists.game.service.UpdateService;
import com.strategists.game.update.payload.UpdatePayload;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
public abstract class AbstractUpdateHandler<T extends UpdatePayload<?>> implements UpdateHandler {

    @Autowired
    private SkipPlayerConfigurationProperties skipPlayerConfigurationProperties;

    @Autowired
    private CleanUpConfigurationProperties cleanUpConfigurationProperties;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UpdateService updateService;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private HistoryService historyService;

    protected Activity saveActivity(Activity activity) {
        return activityRepository.saveAndFlush(activity);
    }

    protected void scheduleSkipPlayerEvent(Game game) {
        if (skipPlayerConfigurationProperties.enabled()) {
            schedulerService.scheduleEvent(SkipPlayerEvent.from(game));
        }
    }

    protected void unscheduleSkipPlayerEvent(Game game) {
        if (skipPlayerConfigurationProperties.enabled()) {
            schedulerService.unscheduleEvent(SkipPlayerEvent.getUniqueIdentifier(game.getCode()));
        }
    }

    protected void scheduleCleanUpEvent(Game game) {
        if (cleanUpConfigurationProperties.enabled()) {
            schedulerService.scheduleEvent(CleanUpEvent.from(game));
        }
    }

    protected void unscheduleCleanUpEvent(Game game) {
        if (cleanUpConfigurationProperties.enabled()) {
            schedulerService.unscheduleEvent(CleanUpEvent.getUniqueIdentifier(game.getCode()));
        }
    }

    protected void sendUpdate(Game game, T update) {
        // Sending update to the UI
        updateService.sendUpdate(game, update);

        // Writing history if enabled
        historyService.appendUpdatePayload(game, update);
    }

    protected void exportHistory(Game game) {
        historyService.exportHistory(game);
    }

    protected void closeEmitters(Game game) {
        updateService.closeEmitters(game);
    }

}
