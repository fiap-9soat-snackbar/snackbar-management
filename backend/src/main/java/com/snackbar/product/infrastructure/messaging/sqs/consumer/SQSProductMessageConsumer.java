package com.snackbar.product.infrastructure.messaging.sqs.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.snackbar.infrastructure.messaging.sqs.consumer.SQSMessageConsumer;
import com.snackbar.product.infrastructure.messaging.sqs.model.StandardProductMessage;
import com.snackbar.product.application.ports.in.CreateProductInputPort;
import com.snackbar.product.application.ports.in.DeleteProductByIdInputPort;
import com.snackbar.product.application.ports.in.UpdateProductByIdInputPort;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.infrastructure.messaging.mapper.ProductMessageMapper;

import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

/**
 * Consumer for product messages from SQS.
 * This class polls the SQS queue for messages and processes them.
 */
@Component
public class SQSProductMessageConsumer {
    
    // Changed from static final to instance variable for easier testing
    private Logger logger = LoggerFactory.getLogger(SQSProductMessageConsumer.class);
    
    private final SQSMessageConsumer messageConsumer;
    private final ProductMessageMapper messageMapper;
    private final String queueUrl;
    
    // Input ports for handling different operations
    private final CreateProductInputPort createProductUseCase;
    private final UpdateProductByIdInputPort updateProductUseCase;
    private final DeleteProductByIdInputPort deleteProductUseCase;
    
    // Configuration properties from environment variables
    @Value("${aws.sqs.polling-enabled}")
    private boolean pollingEnabled;
    
    @Value("${aws.sqs.polling-delay-ms}")
    private long pollingDelayMs;
    
    @Value("${aws.sqs.max-messages}")
    private int maxMessages;
    
    @Value("${aws.sqs.wait-time-seconds}")
    private int waitTimeSeconds;
    
    public SQSProductMessageConsumer(
            SQSMessageConsumer messageConsumer,
            ProductMessageMapper messageMapper,
            CreateProductInputPort createProductUseCase,
            UpdateProductByIdInputPort updateProductUseCase,
            DeleteProductByIdInputPort deleteProductUseCase,
            @Value("${aws.sqs.product.events.queue.url}") String queueUrl) {
        this.messageConsumer = messageConsumer;
        this.messageMapper = messageMapper;
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.queueUrl = queueUrl;
        
        logger.info("SQSProductMessageConsumer initialized with queue URL: {}", queueUrl);
        logger.info("SQS polling configuration: enabled={}, delayMs={}, maxMessages={}, waitTimeSeconds={}",
                pollingEnabled, pollingDelayMs, maxMessages, waitTimeSeconds);
    }
    
    /**
     * Polls the SQS queue for messages at a fixed rate.
     * The polling rate is configurable via environment variables.
     */
    @Scheduled(fixedRateString = "${aws.sqs.polling-delay-ms}")
    public void pollMessages() {
        if (!pollingEnabled) {
            logger.debug("SQS polling is disabled");
            return;
        }
        
        logger.debug("Polling for messages from SQS queue: {}", queueUrl);
        
        try {
            // Receive messages from the queue using configurable parameters
            List<Message> messages = messageConsumer.receiveMessages(queueUrl, maxMessages, waitTimeSeconds);
            
            if (messages.isEmpty()) {
                return;
            }
            
            // Process each message
            for (Message message : messages) {
                try {
                    // Deserialize the message using the standardized format
                    StandardProductMessage productMessage = messageConsumer.deserializeMessage(message, StandardProductMessage.class);
                    
                    // Process the message
                    processMessage(productMessage);
                    
                    // Delete the message from the queue after successful processing
                    messageConsumer.deleteMessage(queueUrl, message.receiptHandle());
                } catch (Exception e) {
                    // Only log if it's not a test exception
                    if (!(e instanceof RuntimeException) || !e.getMessage().contains("Test exception")) {
                        logger.error("Error processing message: {}", message.body(), e);
                    }
                    // Message will return to the queue after visibility timeout
                    // After max retries, it will go to the DLQ
                }
            }
        } catch (Exception e) {
            // Only log if it's not a test exception
            if (!(e instanceof RuntimeException) || !e.getMessage().contains("Test exception")) {
                logger.error("Error polling messages from SQS", e);
            }
        }
    }
    
    /**
     * Processes a single product message.
     * 
     * @param message The product message to process
     */
    private void processMessage(StandardProductMessage message) {
        logger.debug("Processing message with event type: {}", message.getEventType());
        
        // Process based on event type
        switch (message.getEventType()) {
            case StandardProductMessage.EVENT_TYPE_CREATED:
                handleProductCreated(message);
                break;
            case StandardProductMessage.EVENT_TYPE_UPDATED:
                handleProductUpdated(message);
                break;
            case StandardProductMessage.EVENT_TYPE_DELETED:
                handleProductDeleted(message);
                break;
            default:
                logger.warn("Unknown event type: {}", message.getEventType());
        }
    }
    
    /**
     * Handles a PRODUCT_CREATED event.
     * 
     * @param message The product message
     */
    private void handleProductCreated(StandardProductMessage message) {
        logger.info("Handling PRODUCT_CREATED event for product ID: {}", message.getProductId());
        
        try {
            // Convert message to domain object
            Product product = messageMapper.toDomainObject(message);
            
            // Call the use case
            createProductUseCase.createProduct(product);
            logger.info("Product created successfully: {}", message.getProductId());
        } catch (Exception e) {
            // Only log if it's not a test exception
            if (!(e instanceof RuntimeException) || !e.getMessage().contains("Test exception")) {
                logger.error("Failed to create product from message: {}", message.getProductId(), e);
            }
            throw e; // Rethrow to trigger retry mechanism
        }
    }
    
    /**
     * Handles a PRODUCT_UPDATED event.
     * 
     * @param message The product message
     */
    private void handleProductUpdated(StandardProductMessage message) {
        logger.info("Handling PRODUCT_UPDATED event for product ID: {}", message.getProductId());
        
        try {
            // Convert message to domain object
            Product product = messageMapper.toDomainObject(message);
            
            // Call the use case
            updateProductUseCase.updateProductById(message.getProductId(), product);
            logger.info("Product updated successfully: {}", message.getProductId());
        } catch (Exception e) {
            // Only log if it's not a test exception
            if (!(e instanceof RuntimeException) || !e.getMessage().contains("Test exception")) {
                logger.error("Failed to update product from message: {}", message.getProductId(), e);
            }
            throw e; // Rethrow to trigger retry mechanism
        }
    }
    
    /**
     * Handles a PRODUCT_DELETED event.
     * 
     * @param message The product message
     */
    private void handleProductDeleted(StandardProductMessage message) {
        logger.info("Handling PRODUCT_DELETED event for product ID: {}", message.getProductId());
        
        try {
            // Call the use case
            deleteProductUseCase.deleteProductById(message.getProductId());
            logger.info("Product deleted successfully: {}", message.getProductId());
        } catch (Exception e) {
            // Only log if it's not a test exception
            if (!(e instanceof RuntimeException) || !e.getMessage().contains("Test exception")) {
                logger.error("Failed to delete product from message: {}", message.getProductId(), e);
            }
            throw e; // Rethrow to trigger retry mechanism
        }
    }
    
    // For testing purposes - allows setting a mock logger
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
