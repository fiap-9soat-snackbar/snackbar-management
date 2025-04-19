package com.snackbar.product.infrastructure.messaging;

import com.snackbar.product.application.ports.out.DomainEventPublisher;
import com.snackbar.product.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A no-operation implementation of the DomainEventPublisher.
 * This is a temporary implementation until the SQS publisher is implemented.
 */
public class NoOpDomainEventPublisher implements DomainEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(NoOpDomainEventPublisher.class);
    
    @Override
    public void publish(DomainEvent event) {
        logger.info("Event published (no-op): {}", event.getClass().getSimpleName());
    }
}
