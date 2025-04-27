package com.snackbar.infrastructure.messaging.sqs.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snackbar.infrastructure.messaging.sqs.model.SQSMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
        producer = new SQSMessageProducerImpl(objectMapper, sqsClient);
    }

    @Test
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

    @Test
    void sendMessage_shouldThrowException_whenSerializationFails() throws JsonProcessingException {
        // Arrange
        TestSQSMessage message = new TestSQSMessage("TEST_EVENT");
        
        when(objectMapper.writeValueAsString(message))
                .thenThrow(new JsonProcessingException("Serialization failed") {});

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                producer.sendMessage(queueUrl, message));
        
        assertEquals("Failed to serialize message to JSON", exception.getMessage());
        verify(sqsClient, never()).sendMessage(any(SendMessageRequest.class));
    }

    @Test
    void sendMessage_shouldThrowException_whenSendMessageFails() throws JsonProcessingException {
        // Arrange
        TestSQSMessage message = new TestSQSMessage("TEST_EVENT");
        String serializedMessage = "{\"messageId\":\"123\",\"eventType\":\"TEST_EVENT\"}";
        
        when(objectMapper.writeValueAsString(message)).thenReturn(serializedMessage);
        doThrow(new RuntimeException("Send failed")).when(sqsClient).sendMessage(any(SendMessageRequest.class));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                producer.sendMessage(queueUrl, message));
        
        assertEquals("Failed to send message to SQS", exception.getMessage());
    }

    // Test implementation of SQSMessage
    private static class TestSQSMessage extends SQSMessage {
        public TestSQSMessage(String eventType) {
            super("123", eventType, Instant.now());
        }
    }
}
