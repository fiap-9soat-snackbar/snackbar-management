package com.snackbar.iam.infrastructure.event;

import com.snackbar.iam.application.ports.out.IamDomainEventPublisher;
import com.snackbar.iam.domain.event.UserDomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Simple implementation of the IamDomainEventPublisher that uses Spring's ApplicationEventPublisher.
 * This implementation logs events and publishes them to Spring's event system.
 */
@Component
public class SimpleIamDomainEventPublisher implements IamDomainEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleIamDomainEventPublisher.class);
    
    private final ApplicationEventPublisher applicationEventPublisher;
    
    public SimpleIamDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
    
    @Override
    public void publish(UserDomainEvent event) {
        logger.info("Publishing IAM domain event: {} for user {}", event.getEventType(), event.getUserId());
        applicationEventPublisher.publishEvent(event);
    }
}
