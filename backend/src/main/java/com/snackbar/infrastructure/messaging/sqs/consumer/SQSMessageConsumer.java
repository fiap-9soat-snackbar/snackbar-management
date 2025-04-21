package com.snackbar.infrastructure.messaging.sqs.consumer;

import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

/**
 * Interface for receiving messages from SQS.
 * Framework-agnostic with no external dependencies.
 */
public interface SQSMessageConsumer {
    
    /**
     * Receive messages from an SQS queue.
     * 
     * @param queueUrl The URL of the queue
     * @param maxMessages Maximum number of messages to receive
     * @param waitTimeSeconds Time to wait for messages (long polling)
     * @return List of received messages
     */
    List<Message> receiveMessages(String queueUrl, int maxMessages, int waitTimeSeconds);
    
    /**
     * Delete a message from an SQS queue.
     * 
     * @param queueUrl The URL of the queue
     * @param receiptHandle The receipt handle of the message to delete
     */
    void deleteMessage(String queueUrl, String receiptHandle);
    
    /**
     * Deserialize a message body to a specific type.
     * 
     * @param <T> The type to deserialize to
     * @param message The SQS message
     * @param messageType The class of the type to deserialize to
     * @return The deserialized object
     */
    <T> T deserializeMessage(Message message, Class<T> messageType);
}
