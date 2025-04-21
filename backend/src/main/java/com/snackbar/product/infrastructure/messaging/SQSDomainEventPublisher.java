package com.snackbar.product.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.snackbar.infrastructure.messaging.sqs.model.SQSMessage;
import com.snackbar.infrastructure.messaging.sqs.producer.SQSMessageProducer;
import com.snackbar.product.application.ports.out.DomainEventPublisher;
import com.snackbar.product.domain.event.DomainEvent;

import java.util.Arrays;

/**
 * Implementation of DomainEventPublisher that publishes events to AWS SQS.
 */
@Component
@Profile({"aws-local", "dev"})
public class SQSDomainEventPublisher implements DomainEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(SQSDomainEventPublisher.class);
    
    private final ProductMessageMapper messageMapper;
    private final SQSMessageProducer messageProducer;
    private final Environment environment;
    
    @Value("${aws.sqs.product.events.queue.url:#{null}}")
    private String awsQueueUrl;
    
    @Value("${dev.aws.sqs.product.events.queue.url:#{null}}")
    private String devQueueUrl;
    
    public SQSDomainEventPublisher(ProductMessageMapper messageMapper, SQSMessageProducer messageProducer, Environment environment) {
        this.messageMapper = messageMapper;
        this.messageProducer = messageProducer;
        this.environment = environment;
    }
    
    @Override
    public void publish(DomainEvent event) {
        try {
            logger.debug("Publishing event: {}", event.getClass().getSimpleName());
            
            SQSMessage message = messageMapper.toMessage(event);
            
            // Determine which queue URL to use based on active profile
            String queueUrl;
            if (isDevProfileActive()) {
                queueUrl = devQueueUrl;
                logger.debug("Using LocalStack queue URL: {}", queueUrl);
            } else {
                queueUrl = awsQueueUrl;
                logger.debug("Using AWS queue URL: {}", queueUrl);
            }
            
            if (queueUrl == null || queueUrl.isEmpty()) {
                throw new IllegalStateException("SQS queue URL is not configured");
            }
            
            messageProducer.sendMessage(queueUrl, message);
            
            logger.info("Event published to SQS: {}", event.getClass().getSimpleName());
        } catch (Exception e) {
            logger.error("Failed to publish event", e);
            throw e;
        }
    }
    
    private boolean isDevProfileActive() {
        return Arrays.asList(environment.getActiveProfiles()).contains("dev");
    }
}
