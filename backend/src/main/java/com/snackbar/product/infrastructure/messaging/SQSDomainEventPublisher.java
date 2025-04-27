package com.snackbar.product.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.snackbar.infrastructure.messaging.sqs.model.SQSMessage;
import com.snackbar.infrastructure.messaging.sqs.producer.SQSMessageProducer;
import com.snackbar.product.application.ports.out.DomainEventPublisher;
import com.snackbar.product.domain.event.DomainEvent;

/**
 * Implementation of DomainEventPublisher that publishes events to AWS SQS.
 * This implementation will be used when AWS credentials are available.
 */
@Component
public class SQSDomainEventPublisher implements DomainEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(SQSDomainEventPublisher.class);
    
    private final ProductMessageMapper messageMapper;
    private final SQSMessageProducer messageProducer;
    
    @Value("${aws.sqs.product.events.queue.url}")
    private String queueUrl;
    
    public SQSDomainEventPublisher(ProductMessageMapper messageMapper, SQSMessageProducer messageProducer) {
        this.messageMapper = messageMapper;
        this.messageProducer = messageProducer;
    }
    
    @Override
    public void publish(DomainEvent event) {
        try {
            logger.debug("Publishing event: {}", event.getClass().getSimpleName());
            
            SQSMessage message = messageMapper.toMessage(event);
            
            if (queueUrl == null || queueUrl.isEmpty()) {
                throw new IllegalStateException("SQS queue URL is not configured");
            }
            
            messageProducer.sendMessage(queueUrl, message);
            
            logger.info("Event published to SQS: {}", event.getClass().getSimpleName());
        } catch (IllegalStateException e) {
            // Rethrow IllegalStateException without logging as error
            // This allows tests to verify the exception without error logs
            throw e;
        } catch (Exception e) {
            logger.error("Failed to publish event", e);
            throw e; // Rethrow to match test expectations
        }
    }
}
