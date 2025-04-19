package com.snackbar.product.application.ports.out;

import com.snackbar.product.domain.event.DomainEvent;

/**
 * Output port for publishing domain events.
 * This interface defines the contract for publishing domain events to external systems.
 */
public interface DomainEventPublisher {
    
    /**
     * Publishes a domain event.
     *
     * @param event The domain event to publish
     */
    void publish(DomainEvent event);
}
