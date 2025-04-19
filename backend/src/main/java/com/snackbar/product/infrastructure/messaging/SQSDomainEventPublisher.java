package com.snackbar.product.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snackbar.product.application.ports.out.DomainEventPublisher;
import com.snackbar.product.domain.event.DomainEvent;

/**
 * Implementation of DomainEventPublisher that simulates publishing events to SQS.
 * This class converts domain events to SQS messages and logs them.
 */
@Component
@Profile("prod") // Only use this implementation in production profile
public class SQSDomainEventPublisher implements DomainEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(SQSDomainEventPublisher.class);
    
    private final ProductMessageMapper messageMapper;
    private final ObjectMapper objectMapper;
    private final String queueUrl;
    
    public SQSDomainEventPublisher(
            ProductMessageMapper messageMapper,
            ObjectMapper objectMapper,
            @Value("${aws.sqs.product-events-queue-url:https://sqs.us-east-1.amazonaws.com/123456789012/product-events}") String queueUrl) {
        this.messageMapper = messageMapper;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
    }
    
    @Override
    public void publish(DomainEvent event) {
        try {
            ProductMessage message = messageMapper.toMessage(event);
            String messageBody = objectMapper.writeValueAsString(message);
            
            // Log the message instead of sending to SQS
            logger.info("Event would be published to SQS queue {}: {}", 
                    queueUrl, messageBody);
            
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize event to JSON: {}", event.getClass().getSimpleName(), e);
            throw new RuntimeException("Failed to serialize event to JSON", e);
        } catch (Exception e) {
            logger.error("Failed to publish event: {}", event.getClass().getSimpleName(), e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}
