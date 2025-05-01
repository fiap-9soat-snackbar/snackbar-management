package com.snackbar.iam.infrastructure.event;

import com.snackbar.iam.domain.event.UserCreatedEvent;
import com.snackbar.iam.domain.event.UserDeletedEvent;
import com.snackbar.iam.domain.event.UserDomainEvent;
import com.snackbar.iam.domain.event.UserUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener for IAM domain events.
 * This class demonstrates how to handle domain events within the application.
 * In a real-world scenario, this might trigger other business processes or integrations.
 */
@Component
public class IamEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(IamEventListener.class);
    
    /**
     * Handles UserCreatedEvent.
     *
     * @param event The event to handle
     */
    @EventListener
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        logger.info("User created event received: User ID={}, CPF={}, Email={}", 
                event.getUserId(), event.getUserCpf(), event.getUserEmail());
        
        // Here you would implement business logic that should happen when a user is created
        // For example, sending a welcome email, provisioning resources, etc.
    }
    
    /**
     * Handles UserUpdatedEvent.
     *
     * @param event The event to handle
     */
    @EventListener
    public void handleUserUpdatedEvent(UserUpdatedEvent event) {
        logger.info("User updated event received: User ID={}, CPF={}, Email={}", 
                event.getUserId(), event.getUserCpf(), event.getUserEmail());
        
        // Here you would implement business logic that should happen when a user is updated
        // For example, sending a notification, updating related systems, etc.
    }
    
    /**
     * Handles UserDeletedEvent.
     *
     * @param event The event to handle
     */
    @EventListener
    public void handleUserDeletedEvent(UserDeletedEvent event) {
        logger.info("User deleted event received: User ID={}, CPF={}", 
                event.getUserId(), event.getUserCpf());
        
        // Here you would implement business logic that should happen when a user is deleted
        // For example, cleaning up resources, notifying other systems, etc.
    }
    
    /**
     * Handles all UserDomainEvents.
     * This is a catch-all handler that logs all domain events.
     *
     * @param event The event to handle
     */
    @EventListener
    public void handleAllDomainEvents(UserDomainEvent event) {
        logger.debug("Domain event processed: Type={}, User ID={}, Event ID={}", 
                event.getEventType(), event.getUserId(), event.getId());
    }
}
