package com.snackbar.infrastructure.messaging.sqs.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of SQSMessageConsumer that uses the AWS SDK.
 * Spring annotations are used only at this outer layer.
 */
@Component
public class SQSMessageConsumerImpl implements SQSMessageConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(SQSMessageConsumerImpl.class);
    
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
                logger.info("Received {} messages from queue {}", messages.size(), queueUrl);
            } else {
                logger.debug("No messages received from queue {}", queueUrl);
            }
            
            return messages;
        } catch (Exception e) {
            logger.error("Error receiving messages from SQS queue {}", queueUrl, e);
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
            logger.debug("Deleted message with receipt handle: {}", receiptHandle);
        } catch (Exception e) {
            logger.error("Error deleting message from SQS queue {}", queueUrl, e);
            throw new RuntimeException("Failed to delete message from SQS", e);
        }
    }
    
    @Override
    public <T> T deserializeMessage(Message message, Class<T> messageType) {
        try {
            return objectMapper.readValue(message.body(), messageType);
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize message: {}", message.body(), e);
            throw new RuntimeException("Failed to deserialize message", e);
        }
    }
}
