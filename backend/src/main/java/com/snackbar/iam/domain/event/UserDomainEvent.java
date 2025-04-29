package com.snackbar.iam.domain.event;

import java.time.LocalDateTime;

/**
 * Base class for all user-related domain events in the IAM module.
 * Provides common properties and behavior for all user events.
 */
public abstract class UserDomainEvent {
    private final String id;
    private final String userId;
    private final String eventType;
    private final LocalDateTime timestamp;

    protected UserDomainEvent(String userId, String eventType) {
        this.id = java.util.UUID.randomUUID().toString();
        this.userId = userId;
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getEventType() {
        return eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
