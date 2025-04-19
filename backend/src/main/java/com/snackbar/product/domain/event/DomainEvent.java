package com.snackbar.product.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for all domain events.
 * Domain events represent significant occurrences within the domain.
 */
public abstract class DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    
    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}
