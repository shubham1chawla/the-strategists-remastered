package com.strategists.game.service;

import com.strategists.game.listener.event.SchedulableEvent;

public interface SchedulerService {

    void scheduleEvent(SchedulableEvent event);

    void unscheduleEvent(String uniqueIdentifier);

}
