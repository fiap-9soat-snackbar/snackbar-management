package com.snackbar.product.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.snackbar.infrastructure.messaging.sqs.consumer.SQSMessageConsumer;
import com.snackbar.infrastructure.messaging.sqs.model.ProductMessage;
import com.snackbar.product.application.ports.in.CreateProductInputPort;
import com.snackbar.product.application.ports.in.DeleteProductByIdInputPort;
import com.snackbar.product.application.ports.in.UpdateProductByIdInputPort;
import com.snackbar.product.domain.entity.Product;

import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

/**
 * Consumer for product messages from SQS.
 * This class polls the SQS queue for messages and processes them.
 */
@Component
@Profile("prod") // Only use this implementation in production profile
public class SQSProductMessageConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(SQSProductMessageConsumer.class);
    
    private final SQSMessageConsumer messageConsumer;
    private final ProductMessageMapper messageMapper;
    private final String queueUrl;
    
    // Input ports for handling different operations
    private final CreateProductInputPort createProductUseCase;
    private final UpdateProductByIdInputPort updateProductUseCase;
    private final DeleteProductByIdInputPort deleteProductUseCase;
    
    public SQSProductMessageConsumer(
            SQSMessageConsumer messageConsumer,
            ProductMessageMapper messageMapper,
            CreateProductInputPort createProductUseCase,
            UpdateProductByIdInputPort updateProductUseCase,
            DeleteProductByIdInputPort deleteProductUseCase,
            @Value("${aws.sqs.product-events-queue-url}") String queueUrl) {
        this.messageConsumer = messageConsumer;
        this.messageMapper = messageMapper;
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.queueUrl = queueUrl;
        
        logger.info("SQSProductMessageConsumer initialized with queue URL: {}", queueUrl);
    }
    
    /**
     * Polls the SQS queue for messages at a fixed rate.
     * This method is scheduled to run every 10 seconds.
     */
    @Scheduled(fixedRate = 10000) // Poll every 10 seconds
    public void pollMessages() {
        logger.debug("Polling for messages from SQS queue: {}", queueUrl);
        
        try {
            // Receive messages from the queue
            List<Message> messages = messageConsumer.receiveMessages(queueUrl, 10, 5);
            
            if (messages.isEmpty()) {
                return;
            }
            
            // Process each message
            for (Message message : messages) {
                try {
                    // Deserialize the message
                    ProductMessage productMessage = messageConsumer.deserializeMessage(message, ProductMessage.class);
                    
                    // Process the message
                    processMessage(productMessage);
                    
                    // Delete the message from the queue after successful processing
                    messageConsumer.deleteMessage(queueUrl, message.receiptHandle());
                } catch (Exception e) {
                    logger.error("Error processing message: {}", message.body(), e);
                    // Message will return to the queue after visibility timeout
                }
            }
        } catch (Exception e) {
            logger.error("Error polling messages from SQS", e);
        }
    }
    
    /**
     * Processes a single product message.
     * 
     * @param message The product message to process
     */
    private void processMessage(ProductMessage message) {
        logger.debug("Processing message with event type: {}", message.getEventType());
        
        // Process based on event type
        switch (message.getEventType()) {
            case ProductMessage.EVENT_TYPE_CREATED:
                handleProductCreated(message);
                break;
            case ProductMessage.EVENT_TYPE_UPDATED:
                handleProductUpdated(message);
                break;
            case ProductMessage.EVENT_TYPE_DELETED:
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
    private void handleProductCreated(ProductMessage message) {
        logger.info("Handling PRODUCT_CREATED event for product ID: {}", message.getProductId());
        
        // Convert message to domain object
        Product product = messageMapper.toDomainObject(message);
        
        // Call the use case
        createProductUseCase.createProduct(product);
    }
    
    /**
     * Handles a PRODUCT_UPDATED event.
     * 
     * @param message The product message
     */
    private void handleProductUpdated(ProductMessage message) {
        logger.info("Handling PRODUCT_UPDATED event for product ID: {}", message.getProductId());
        
        // Convert message to domain object
        Product product = messageMapper.toDomainObject(message);
        
        // Call the use case
        updateProductUseCase.updateProductById(message.getProductId(), product);
    }
    
    /**
     * Handles a PRODUCT_DELETED event.
     * 
     * @param message The product message
     */
    private void handleProductDeleted(ProductMessage message) {
        logger.info("Handling PRODUCT_DELETED event for product ID: {}", message.getProductId());
        
        // Call the use case
        deleteProductUseCase.deleteProductById(message.getProductId());
    }
}
