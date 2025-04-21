package com.snackbar.infrastructure.messaging.sqs.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for all SQS messages.
 * This class provides common fields for all SQS messages.
 * Framework-agnostic with no external dependencies.
 */
public abstract class SQSMessage {
    
    private String messageId;
    private String eventType;
    private Instant timestamp;
    
    /**
     * Default constructor for deserialization.
     */
    public SQSMessage() {
        this.messageId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
    }
    
    /**
     * Constructor with event type.
     * 
     * @param eventType The event type
     */
    public SQSMessage(String eventType) {
        this();
        this.eventType = eventType;
    }
    
    /**
     * Constructor with all fields.
     * 
     * @param messageId The message ID
     * @param eventType The event type
     * @param timestamp The timestamp
     */
    public SQSMessage(String messageId, String eventType, Instant timestamp) {
        this.messageId = messageId;
        this.eventType = eventType;
        this.timestamp = timestamp;
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
