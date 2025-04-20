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

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

/**
 * Implementation of DomainEventPublisher that publishes events to AWS SQS.
 */
@Component
@Profile("prod") // Only use this implementation in production profile
public class SQSDomainEventPublisher implements DomainEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(SQSDomainEventPublisher.class);
    
    private final ProductMessageMapper messageMapper;
    private final ObjectMapper objectMapper;
    private final String queueUrl;
    private final SqsClient sqsClient;
    
    public SQSDomainEventPublisher(
            ProductMessageMapper messageMapper,
            ObjectMapper objectMapper,
            SqsClient sqsClient,
            @Value("${aws.sqs.product-events-queue-url}") String queueUrl) {
        this.messageMapper = messageMapper;
        this.objectMapper = objectMapper;
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
        
        logger.info("SQSDomainEventPublisher initialized with queue URL: {}", queueUrl);
    }
    
    @Override
    public void publish(DomainEvent event) {
        try {
            ProductMessage message = messageMapper.toMessage(event);
            String messageBody = objectMapper.writeValueAsString(message);
            
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build();
                
            sqsClient.sendMessage(sendMessageRequest);
            
            logger.info("Event published to SQS queue {}: {}", 
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
