package com.snackbar.infrastructure.messaging.sqs.producer;

import com.snackbar.infrastructure.messaging.sqs.model.SQSMessage;

/**
 * Interface for sending messages to SQS.
 */
public interface SQSMessageProducer {
    
    /**
     * Sends a message to an SQS queue.
     * 
     * @param queueUrl The URL of the SQS queue
     * @param message The message to send
     * @throws RuntimeException if the message cannot be sent
     */
    void sendMessage(String queueUrl, SQSMessage message);
}
