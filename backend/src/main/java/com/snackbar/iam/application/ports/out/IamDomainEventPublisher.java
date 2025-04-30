package com.snackbar.iam.application.ports.out;

import com.snackbar.iam.domain.event.UserDomainEvent;

/**
 * Output port for publishing IAM domain events.
 * This interface defines how domain events are published to the outside world.
 * Named with "Iam" prefix to avoid bean naming conflicts with other modules.
 */
public interface IamDomainEventPublisher {
    /**
     * Publishes a domain event.
     *
     * @param event The domain event to publish
     */
    void publish(UserDomainEvent event);
}
