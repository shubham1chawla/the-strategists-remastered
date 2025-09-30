package com.strategists.game.listener.event;

import java.time.Instant;

public interface SchedulableEvent {

    /**
     * Schedulable events can be unscheduled from being published. The unique identifier helps identify its
     * corresponding future task to cancel.
     *
     * @return unique identifier
     */
    String getUniqueIdentifier();

    Instant getScheduledTime();

}
