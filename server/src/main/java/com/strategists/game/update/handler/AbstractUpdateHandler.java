package com.strategists.game.update.handler;

import com.strategists.game.entity.Activity;
import com.strategists.game.entity.Game;
import com.strategists.game.listener.event.AdvicesServiceEvent;
import com.strategists.game.listener.event.CleanUpEvent;
import com.strategists.game.listener.event.PredictionsServiceEvent;
import com.strategists.game.listener.event.SkipPlayerEvent;
import com.strategists.game.repository.ActivityRepository;
import com.strategists.game.repository.TrendRepository;
import com.strategists.game.service.AdvicesService;
import com.strategists.game.service.PredictionsService;
import com.strategists.game.service.SchedulerService;
import com.strategists.game.service.UpdateService;
import com.strategists.game.update.payload.UpdatePayload;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Objects;

@Log4j2
public abstract class AbstractUpdateHandler<T extends UpdatePayload<?>> implements UpdateHandler {

    @Value("${strategists.skip-player.enabled}")
    private boolean isSkipPlayerEnabled;

    @Value("${strategists.clean-up.enabled}")
    private boolean isCleanUpEnabled;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private TrendRepository trendRepository;

    @Autowired
    private UpdateService updateService;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired(required = false)
    private PredictionsService predictionsService;

    @Autowired(required = false)
    private AdvicesService advicesService;

    protected Activity saveActivity(Activity activity) {
        return activityRepository.saveAndFlush(activity);
    }

    protected void reset(Game game) {
        activityRepository.deleteByGame(game);
        trendRepository.deleteByGame(game);
        if (Objects.nonNull(predictionsService)) {
            predictionsService.clearPlayerPredictions(game);
        }
        if (Objects.nonNull(advicesService)) {
            advicesService.clearAdvices(game);
        }
    }

    protected void publishTrainPredictionsModelEvent(Game game) {
        if (Objects.nonNull(predictionsService)) {
            eventPublisher.publishEvent(PredictionsServiceEvent.forTrain(game));
        }
    }

    protected void publishInferPredictionsModelEvent(Game game) {
        if (Objects.nonNull(predictionsService)) {
            eventPublisher.publishEvent(PredictionsServiceEvent.forInfer(game));
        }
    }

    protected void publishGenerateAdvicesEvent(Game game) {
        if (Objects.nonNull(advicesService)) {
            eventPublisher.publishEvent(AdvicesServiceEvent.forGenerate(game));
        }
    }

    protected void publishExportAdvicesEvent(Game game) {
        if (Objects.nonNull(advicesService)) {
            eventPublisher.publishEvent(AdvicesServiceEvent.forExport(game));
        }
    }

    protected void scheduleSkipPlayerEvent(Game game) {
        if (isSkipPlayerEnabled) {
            schedulerService.scheduleEvent(SkipPlayerEvent.from(game));
        }
    }

    protected void unscheduleSkipPlayerEvent(Game game) {
        if (isSkipPlayerEnabled) {
            schedulerService.unscheduleEvent(SkipPlayerEvent.getUniqueIdentifier(game.getCode()));
        }
    }

    protected void scheduleCleanUpEvent(Game game) {
        if (isCleanUpEnabled) {
            schedulerService.scheduleEvent(CleanUpEvent.from(game));
        }
    }

    protected void unscheduleCleanUpEvent(Game game) {
        if (isCleanUpEnabled) {
            schedulerService.unscheduleEvent(CleanUpEvent.getUniqueIdentifier(game.getCode()));
        }
    }

    protected void sendUpdate(Game game, T update) {
        updateService.sendUpdate(game, update);
    }

    protected void closeEmitters(Game game) {
        updateService.closeEmitters(game);
    }

}
