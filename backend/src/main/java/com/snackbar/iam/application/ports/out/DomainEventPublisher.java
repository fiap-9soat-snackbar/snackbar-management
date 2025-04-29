package com.snackbar.iam.application.ports.out;

import com.snackbar.iam.domain.event.UserDomainEvent;

/**
 * Output port for publishing domain events.
 * This interface defines how domain events are published to the outside world.
 */
public interface DomainEventPublisher {
    /**
     * Publishes a domain event.
     *
     * @param event The domain event to publish
     */
    void publish(UserDomainEvent event);
}
