package com.snackbar.infrastructure.messaging.sqs.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snackbar.infrastructure.messaging.sqs.model.SQSMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SQS Message Producer Tests")
class SQSMessageProducerImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SqsClient sqsClient;
    
    @Captor
    private ArgumentCaptor<SendMessageRequest> sendRequestCaptor;

    private SQSMessageProducerImpl producer;
    private final String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue";

    @BeforeEach
    void setUp() {
        // Replace the real logger with a mock to prevent error logs during tests
        try {
            // Get the logger field using reflection
            java.lang.reflect.Field loggerField = SQSMessageProducerImpl.class.getDeclaredField("log");
            loggerField.setAccessible(true);
            
            // Create a mock logger
            Logger mockLogger = mock(Logger.class);
            
            // Create the producer
            producer = new SQSMessageProducerImpl(objectMapper, sqsClient);
            
            // Replace the real logger with our mock
            loggerField.set(producer, mockLogger);
        } catch (Exception e) {
            // If reflection fails, create the producer normally
            producer = new SQSMessageProducerImpl(objectMapper, sqsClient);
        }
    }

    @Nested
    @DisplayName("When sending messages successfully")
    class WhenSendingMessagesSuccessfully {
        
        @Test
        @DisplayName("Should send message when parameters are valid")
        void sendMessage_shouldSendMessage_whenValidParameters() throws JsonProcessingException {
            // Arrange
            TestSQSMessage message = new TestSQSMessage("TEST_EVENT");
            String serializedMessage = "{\"messageId\":\"123\",\"eventType\":\"TEST_EVENT\"}";
            
            when(objectMapper.writeValueAsString(message)).thenReturn(serializedMessage);
            
            SendMessageResponse response = SendMessageResponse.builder()
                    .messageId("msg-123")
                    .build();
            
            // Capture the request to verify it later
            doReturn(response).when(sqsClient).sendMessage(sendRequestCaptor.capture());

            // Act
            producer.sendMessage(queueUrl, message);

            // Assert
            verify(objectMapper).writeValueAsString(message);
            
            SendMessageRequest capturedRequest = sendRequestCaptor.getValue();
            assertEquals(queueUrl, capturedRequest.queueUrl());
            assertEquals(serializedMessage, capturedRequest.messageBody());
        }
    }

    @Nested
    @DisplayName("When handling error conditions")
    class WhenHandlingErrorConditions {
        
        @Test
        @DisplayName("Should throw exception when serialization fails")
        void sendMessage_shouldThrowException_whenSerializationFails() throws JsonProcessingException {
            // Arrange
            TestSQSMessage message = new TestSQSMessage("TEST_EVENT");
            
            // Create a custom exception that doesn't log stack traces in tests
            JsonProcessingException testException = new JsonProcessingException("Serialization failed") {};
            when(objectMapper.writeValueAsString(message)).thenThrow(testException);

            // Act & Assert
            Exception exception = assertThrows(RuntimeException.class, () ->
                    producer.sendMessage(queueUrl, message));
            
            assertEquals("Failed to serialize message to JSON", exception.getMessage());
            verify(sqsClient, never()).sendMessage(any(SendMessageRequest.class));
        }

        @Test
        @DisplayName("Should throw exception when send message fails")
        void sendMessage_shouldThrowException_whenSendMessageFails() throws JsonProcessingException {
            // Arrange
            TestSQSMessage message = new TestSQSMessage("TEST_EVENT");
            String serializedMessage = "{\"messageId\":\"123\",\"eventType\":\"TEST_EVENT\"}";
            
            when(objectMapper.writeValueAsString(message)).thenReturn(serializedMessage);
            
            // Create a runtime exception without a real stack trace for testing
            RuntimeException testException = new RuntimeException("Send failed");
            doThrow(testException).when(sqsClient).sendMessage(any(SendMessageRequest.class));

            // Act & Assert
            Exception exception = assertThrows(RuntimeException.class, () ->
                    producer.sendMessage(queueUrl, message));
            
            assertEquals("Failed to send message to SQS", exception.getMessage());
        }
    }

    // Test implementation of SQSMessage
    private static class TestSQSMessage extends SQSMessage {
        public TestSQSMessage(String eventType) {
            super("123", eventType, Instant.now());
        }
    }
}
