package com.snackbar.infrastructure.messaging.sqs.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snackbar.infrastructure.messaging.sqs.model.SQSMessage;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

/**
 * Implementation of SQSMessageProducer that sends messages to SQS.
 */
@Component
public class SQSMessageProducerImpl implements SQSMessageProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(SQSMessageProducerImpl.class);
    
    private final ObjectMapper objectMapper;
    private final SqsClient sqsClient;
    
    public SQSMessageProducerImpl(ObjectMapper objectMapper, SqsClient sqsClient) {
        this.objectMapper = objectMapper;
        this.sqsClient = sqsClient;
        logger.info("SQSMessageProducerImpl initialized with SqsClient: {}", sqsClient);
    }
    
    @Override
    public void sendMessage(String queueUrl, SQSMessage message) {
        try {
            String messageBody = objectMapper.writeValueAsString(message);
            
            logger.info("Sending message to SQS queue: {}", queueUrl);
            logger.debug("Message body: {}", messageBody);
            
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build();
            
            logger.debug("SQS client: {}", sqsClient);
            
            try {
                SendMessageResponse response = sqsClient.sendMessage(sendMessageRequest);
                logger.info("Message sent to SQS. MessageId: {}, EventType: {}", 
                        response.messageId(), message.getEventType());
            } catch (Exception e) {
                logger.error("Failed to send message to SQS. Error details: {}", e.toString(), e);
                throw new RuntimeException("Failed to send message to SQS", e);
            }
            
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize message to JSON", e);
            throw new RuntimeException("Failed to serialize message to JSON", e);
        } catch (Exception e) {
            logger.error("Failed to send message to SQS", e);
            throw new RuntimeException("Failed to send message to SQS", e);
        }
    }
}
