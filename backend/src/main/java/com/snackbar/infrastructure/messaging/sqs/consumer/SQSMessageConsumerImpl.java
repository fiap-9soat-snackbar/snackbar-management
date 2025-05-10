package com.snackbar.infrastructure.messaging.sqs.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.Collections;
import java.util.List;

import com.snackbar.product.infrastructure.messaging.sqs.model.StandardProductMessage;

/**
 * Implementation of SQSMessageConsumer that uses the AWS SDK.
 * Spring annotations are used only at this outer layer.
 */
@Component
public class SQSMessageConsumerImpl implements SQSMessageConsumer {
    
    // Using instance logger instead of static logger to make testing easier
    private Logger log = LoggerFactory.getLogger(SQSMessageConsumerImpl.class);
    
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    
    public SQSMessageConsumerImpl(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public List<Message> receiveMessages(String queueUrl, int maxMessages, int waitTimeSeconds) {
        try {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(maxMessages)
                .waitTimeSeconds(waitTimeSeconds)
                .build();
            
            ReceiveMessageResponse response = sqsClient.receiveMessage(receiveRequest);
            List<Message> messages = response.messages();
            
            if (!messages.isEmpty()) {
                log.info("Received {} messages from queue {}", messages.size(), queueUrl);
            } else {
                log.debug("No messages received from queue {}", queueUrl);
            }
            
            return messages;
        } catch (Exception e) {
            // In test environments, we might expect certain exceptions
            // Only log the error message at ERROR level, full stack trace at DEBUG level
            log.error("Error receiving messages from SQS queue {}", queueUrl);
            log.debug("Full exception details", e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public void deleteMessage(String queueUrl, String receiptHandle) {
        try {
            DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build();
            
            sqsClient.deleteMessage(deleteRequest);
            log.debug("Deleted message with receipt handle: {}", receiptHandle);
        } catch (Exception e) {
            // Only log the error message at ERROR level, full stack trace at DEBUG level
            log.error("Error deleting message from SQS queue {}", queueUrl);
            log.debug("Full exception details", e);
            throw new RuntimeException("Failed to delete message from SQS", e);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserializeMessage(Message message, Class<T> messageType) {
        try {
            // For StandardProductMessage, handle different formats
            if (messageType.equals(StandardProductMessage.class)) {
                return (T) deserializeStandardProductMessage(message.body());
            }
            
            // Default deserialization for other types
            return objectMapper.readValue(message.body(), messageType);
        } catch (JsonProcessingException e) {
            // Only log the error message at ERROR level, full stack trace at DEBUG level
            log.error("Failed to deserialize message: {}", message.body());
            log.debug("Full exception details", e);
            throw new RuntimeException("Failed to deserialize message", e);
        }
    }
    
    /**
     * Deserialize a message to StandardProductMessage, handling different formats.
     * 
     * @param messageBody The message body
     * @return A StandardProductMessage
     * @throws JsonProcessingException If deserialization fails
     */
    private StandardProductMessage deserializeStandardProductMessage(String messageBody) throws JsonProcessingException {
        try {
            // Try to parse as JsonNode first to inspect the structure
            JsonNode rootNode = objectMapper.readTree(messageBody);
            
            // Check if this is a legacy format with productData field
            if (rootNode.has("productData")) {
                StandardProductMessage message = new StandardProductMessage();
                message.setMessageId(rootNode.path("messageId").asText());
                message.setEventType(rootNode.path("eventType").asText());
                
                if (rootNode.has("timestamp")) {
                    message.setTimestamp(java.time.Instant.ofEpochSecond(
                        rootNode.path("timestamp").asLong(), 
                        (long)(rootNode.path("timestamp").asDouble() % 1 * 1_000_000_000L)
                    ));
                }
                
                JsonNode productData = rootNode.path("productData");
                message.setProductId(productData.path("id").asText());
                message.setName(productData.path("name").asText());
                message.setCategory(productData.path("category").asText());
                message.setDescription(productData.path("description").asText());
                
                if (productData.has("price")) {
                    message.setPrice(new java.math.BigDecimal(productData.path("price").asText()));
                }
                
                if (productData.has("cookingTime")) {
                    message.setCookingTime(productData.path("cookingTime").asInt());
                }
                
                return message;
            }
            
            // Standard format - direct mapping
            return objectMapper.readValue(messageBody, StandardProductMessage.class);
        } catch (Exception e) {
            // Only log the error message at ERROR level, full stack trace at DEBUG level
            log.error("Error deserializing message to StandardProductMessage: {}", messageBody);
            log.debug("Full exception details", e);
            throw e;
        }
    }
    
    // For testing purposes - allows setting a mock logger
    public void setLogger(Logger logger) {
        this.log = logger;
    }
}
