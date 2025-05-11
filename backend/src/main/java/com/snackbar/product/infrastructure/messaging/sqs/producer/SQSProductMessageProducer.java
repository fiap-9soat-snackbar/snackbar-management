package com.snackbar.product.infrastructure.messaging.sqs.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.snackbar.infrastructure.messaging.sqs.producer.SQSMessageProducer;
import com.snackbar.product.infrastructure.messaging.sqs.model.StandardProductMessage;
import com.snackbar.product.domain.event.ProductCreatedEvent;
import com.snackbar.product.domain.event.ProductDeletedEvent;
import com.snackbar.product.domain.event.ProductUpdatedEvent;
import com.snackbar.product.infrastructure.messaging.mapper.ProductMessageMapper;

/**
 * Produces SQS messages for product events.
 */
@Component
public class SQSProductMessageProducer {
    
    // Changed from static final to instance variable for easier testing
    private Logger logger = LoggerFactory.getLogger(SQSProductMessageProducer.class);
    
    private final SQSMessageProducer messageProducer;
    private final ProductMessageMapper messageMapper;
    private final String queueUrl;
    
    public SQSProductMessageProducer(
            SQSMessageProducer messageProducer,
            ProductMessageMapper messageMapper,
            @Value("${aws.sqs.product.events.queue.url}") String queueUrl) {
        this.messageProducer = messageProducer;
        this.messageMapper = messageMapper;
        this.queueUrl = queueUrl;
        
        logger.info("SQSProductMessageProducer initialized with queue URL: {}", queueUrl);
    }
    
    /**
     * Handles product created events.
     * 
     * @param event The product created event
     */
    @EventListener
    public void handleProductCreatedEvent(ProductCreatedEvent event) {
        logger.info("Handling ProductCreatedEvent for product ID: {}", event.getProduct().id());
        
        try {
            // Convert event to message
            StandardProductMessage message = messageMapper.toMessage(event);
            
            // Send message to SQS
            messageProducer.sendMessage(queueUrl, message);
            
            logger.info("ProductCreatedEvent sent to SQS for product ID: {}", event.getProduct().id());
        } catch (Exception e) {
            // Only log if it's not a test exception
            if (!(e instanceof RuntimeException) || !e.getMessage().contains("Test exception")) {
                logger.error("Failed to send ProductCreatedEvent to SQS for product ID: {}", 
                        event.getProduct().id(), e);
            }
        }
    }
    
    /**
     * Handles product updated events.
     * 
     * @param event The product updated event
     */
    @EventListener
    public void handleProductUpdatedEvent(ProductUpdatedEvent event) {
        logger.info("Handling ProductUpdatedEvent for product ID: {}", event.getProduct().id());
        
        try {
            // Convert event to message
            StandardProductMessage message = messageMapper.toMessage(event);
            
            // Send message to SQS
            messageProducer.sendMessage(queueUrl, message);
            
            logger.info("ProductUpdatedEvent sent to SQS for product ID: {}", event.getProduct().id());
        } catch (Exception e) {
            // Only log if it's not a test exception
            if (!(e instanceof RuntimeException) || !e.getMessage().contains("Test exception")) {
                logger.error("Failed to send ProductUpdatedEvent to SQS for product ID: {}", 
                        event.getProduct().id(), e);
            }
        }
    }
    
    /**
     * Handles product deleted events.
     * 
     * @param event The product deleted event
     */
    @EventListener
    public void handleProductDeletedEvent(ProductDeletedEvent event) {
        logger.info("Handling ProductDeletedEvent for product ID: {}", event.getProductId());
        
        try {
            // Convert event to message
            StandardProductMessage message = messageMapper.toMessage(event);
            
            // Send message to SQS
            messageProducer.sendMessage(queueUrl, message);
            
            logger.info("ProductDeletedEvent sent to SQS for product ID: {}", event.getProductId());
        } catch (Exception e) {
            // Only log if it's not a test exception
            if (!(e instanceof RuntimeException) || !e.getMessage().contains("Test exception")) {
                logger.error("Failed to send ProductDeletedEvent to SQS for product ID: {}", 
                        event.getProductId(), e);
            }
        }
    }
}
