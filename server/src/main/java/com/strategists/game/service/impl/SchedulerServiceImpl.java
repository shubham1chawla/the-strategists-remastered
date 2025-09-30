package com.strategists.game.service.impl;

import com.strategists.game.listener.event.SchedulableEvent;
import com.strategists.game.service.SchedulerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Log4j2
@Service
public class SchedulerServiceImpl implements SchedulerService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private TaskScheduler scheduler;

    private final Map<String, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();

    @Override
    public void scheduleEvent(SchedulableEvent event) {
       // Unscheduling previous event with same unique identifier
       unscheduleEvent(event.getUniqueIdentifier());

       // Scheduling event
       log.info("Scheduling event: {}", event.getUniqueIdentifier());
       final var future = scheduler.schedule(() -> eventPublisher.publishEvent(event), event.getScheduledTime());
       futures.put(event.getUniqueIdentifier(), future);
    }

    @Override
    public void unscheduleEvent(String uniqueIdentifier) {
        if (!futures.containsKey(uniqueIdentifier)) {
            return;
        }

        // Cancelling future
        log.info("Unscheduling event: {}", uniqueIdentifier);
        final var future = futures.get(uniqueIdentifier);
        future.cancel(true);
        futures.remove(uniqueIdentifier);
    }

}
