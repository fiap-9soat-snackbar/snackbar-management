package com.snackbar.infrastructure.messaging.sqs.mapper;

import com.snackbar.infrastructure.messaging.sqs.model.SQSMessage;

/**
 * Generic interface for mapping between domain objects and SQS messages.
 * Framework-agnostic with no external dependencies.
 * 
 * @param <D> Domain object type
 * @param <M> Message type
 */
public interface MessageMapper<D, M extends SQSMessage> {
    
    /**
     * Convert a domain object to an SQS message.
     * 
     * @param domainObject The domain object to convert
     * @param eventType The type of event (created, updated, deleted, etc.)
     * @return The SQS message
     */
    M toMessage(D domainObject, String eventType);
    
    /**
     * Convert an SQS message to a domain object.
     * 
     * @param message The SQS message to convert
     * @return The domain object
     */
    D toDomainObject(M message);
}
