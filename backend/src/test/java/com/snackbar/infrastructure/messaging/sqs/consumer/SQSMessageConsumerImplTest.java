package com.snackbar.infrastructure.messaging.sqs.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snackbar.product.infrastructure.messaging.sqs.model.StandardProductMessage;
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
import software.amazon.awssdk.services.sqs.model.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SQS Message Consumer Tests")
class SQSMessageConsumerImplTest {

    @Mock
    private SqsClient sqsClient;

    @Mock
    private ObjectMapper objectMapper;
    
    @Mock
    private Logger mockLogger;
    
    @Captor
    private ArgumentCaptor<ReceiveMessageRequest> receiveRequestCaptor;
    
    @Captor
    private ArgumentCaptor<DeleteMessageRequest> deleteRequestCaptor;

    private SQSMessageConsumerImpl consumer;
    private final String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue";
    private final String receiptHandle = "receipt-handle-123";

    @BeforeEach
    void setUp() {
        // Create the consumer
        consumer = new SQSMessageConsumerImpl(sqsClient, objectMapper);
        
        // Set the mock logger using the instance method
        consumer.setLogger(mockLogger);
    }

    @Nested
    @DisplayName("When receiving messages")
    class WhenReceivingMessages {
        
        @Test
        @DisplayName("Should return messages when they exist in the queue")
        void receiveMessages_shouldReturnMessages_whenMessagesExist() {
            // Arrange
            int maxMessages = 10;
            int waitTimeSeconds = 5;
            Message message = Message.builder()
                    .body("{\"test\":\"message\"}")
                    .receiptHandle(receiptHandle)
                    .build();
            
            ReceiveMessageResponse response = ReceiveMessageResponse.builder()
                    .messages(message)
                    .build();
            
            // Capture the request to verify it later
            when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(response);

            // Act
            List<Message> result = consumer.receiveMessages(queueUrl, maxMessages, waitTimeSeconds);

            // Assert
            assertEquals(1, result.size());
            assertEquals("{\"test\":\"message\"}", result.get(0).body());
            
            // Verify the request was built correctly
            verify(sqsClient).receiveMessage(receiveRequestCaptor.capture());
            ReceiveMessageRequest capturedRequest = receiveRequestCaptor.getValue();
            assertEquals(queueUrl, capturedRequest.queueUrl());
            assertEquals(maxMessages, capturedRequest.maxNumberOfMessages());
            assertEquals(waitTimeSeconds, capturedRequest.waitTimeSeconds());
        }

        @Test
        @DisplayName("Should return empty list when no messages exist")
        void receiveMessages_shouldReturnEmptyList_whenNoMessagesExist() {
            // Arrange
            ReceiveMessageResponse response = ReceiveMessageResponse.builder()
                    .messages(Collections.emptyList())
                    .build();
            
            when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(response);

            // Act
            List<Message> result = consumer.receiveMessages(queueUrl, 10, 5);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty list when exception occurs")
        void receiveMessages_shouldReturnEmptyList_whenExceptionOccurs() {
            // Arrange
            RuntimeException testException = new RuntimeException("Test exception");
            when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class))).thenThrow(testException);

            // Act
            List<Message> result = consumer.receiveMessages(queueUrl, 10, 5);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("When deleting messages")
    class WhenDeletingMessages {
        
        @Test
        @DisplayName("Should delete message when parameters are valid")
        void deleteMessage_shouldDeleteMessage_whenValidParameters() {
            // Act
            consumer.deleteMessage(queueUrl, receiptHandle);

            // Assert
            verify(sqsClient).deleteMessage(deleteRequestCaptor.capture());
            DeleteMessageRequest capturedRequest = deleteRequestCaptor.getValue();
            assertEquals(queueUrl, capturedRequest.queueUrl());
            assertEquals(receiptHandle, capturedRequest.receiptHandle());
        }

        @Test
        @DisplayName("Should throw exception when delete fails")
        void deleteMessage_shouldThrowException_whenDeleteFails() {
            // Arrange
            RuntimeException testException = new RuntimeException("Delete failed");
            doThrow(testException).when(sqsClient).deleteMessage(any(DeleteMessageRequest.class));

            // Act & Assert
            Exception exception = assertThrows(RuntimeException.class, () ->
                    consumer.deleteMessage(queueUrl, receiptHandle));
            
            assertEquals("Failed to delete message from SQS", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("When deserializing messages")
    class WhenDeserializingMessages {
        
        @Test
        @DisplayName("Should deserialize standard message when JSON is valid")
        void deserializeMessage_shouldDeserializeStandardMessage_whenValidJson() throws JsonProcessingException {
            // Arrange
            String messageBody = "{\"messageId\":\"123\",\"eventType\":\"PRODUCT_CREATED\",\"timestamp\":\"2023-01-01T12:00:00Z\"}";
            Message message = Message.builder().body(messageBody).build();
            StandardProductMessage expectedMessage = new StandardProductMessage();
            
            // Mock direct deserialization
            when(objectMapper.readValue(eq(messageBody), eq(StandardProductMessage.class))).thenReturn(expectedMessage);
            
            // Mock readTree to avoid NullPointerException
            JsonNode mockNode = mock(JsonNode.class);
            when(objectMapper.readTree(messageBody)).thenReturn(mockNode);
            when(mockNode.has("productData")).thenReturn(false);

            // Act
            StandardProductMessage result = consumer.deserializeMessage(message, StandardProductMessage.class);

            // Assert
            assertSame(expectedMessage, result);
            verify(objectMapper).readValue(messageBody, StandardProductMessage.class);
        }

        @Test
        @DisplayName("Should handle legacy format when productData exists")
        void deserializeMessage_shouldHandleLegacyFormat_whenProductDataExists() throws Exception {
            // Arrange
            String legacyMessageBody = "{\"messageId\":\"123\",\"eventType\":\"PRODUCT_CREATED\",\"timestamp\":1672574400.0," +
                    "\"productData\":{\"id\":\"p123\",\"name\":\"Test Product\",\"category\":\"FOOD\",\"description\":\"Test Description\"," +
                    "\"price\":\"10.99\",\"cookingTime\":15}}";
            
            Message message = Message.builder().body(legacyMessageBody).build();
            
            // Mock the objectMapper.readValue to throw exception to force legacy path
            JsonProcessingException testException = new JsonProcessingException("Force legacy path") {};
            when(objectMapper.readValue(eq(legacyMessageBody), eq(StandardProductMessage.class)))
                    .thenThrow(testException);
            
            // Mock the objectMapper.readTree() method
            JsonNode rootNode = mock(JsonNode.class);
            JsonNode productDataNode = mock(JsonNode.class);
            JsonNode messageIdNode = mock(JsonNode.class);
            JsonNode eventTypeNode = mock(JsonNode.class);
            JsonNode timestampNode = mock(JsonNode.class);
            JsonNode idNode = mock(JsonNode.class);
            JsonNode nameNode = mock(JsonNode.class);
            JsonNode categoryNode = mock(JsonNode.class);
            JsonNode descriptionNode = mock(JsonNode.class);
            JsonNode priceNode = mock(JsonNode.class);
            JsonNode cookingTimeNode = mock(JsonNode.class);
            
            when(objectMapper.readTree(legacyMessageBody)).thenReturn(rootNode);
            when(rootNode.has("productData")).thenReturn(true);
            when(rootNode.path("messageId")).thenReturn(messageIdNode);
            when(messageIdNode.asText()).thenReturn("123");
            
            when(rootNode.path("eventType")).thenReturn(eventTypeNode);
            when(eventTypeNode.asText()).thenReturn("PRODUCT_CREATED");
            
            when(rootNode.has("timestamp")).thenReturn(true);
            when(rootNode.path("timestamp")).thenReturn(timestampNode);
            when(timestampNode.asDouble()).thenReturn(1672574400.0);
            when(timestampNode.asLong()).thenReturn(1672574400L);
            
            when(rootNode.path("productData")).thenReturn(productDataNode);
            
            when(productDataNode.path("id")).thenReturn(idNode);
            when(idNode.asText()).thenReturn("p123");
            
            when(productDataNode.path("name")).thenReturn(nameNode);
            when(nameNode.asText()).thenReturn("Test Product");
            
            when(productDataNode.path("category")).thenReturn(categoryNode);
            when(categoryNode.asText()).thenReturn("FOOD");
            
            when(productDataNode.path("description")).thenReturn(descriptionNode);
            when(descriptionNode.asText()).thenReturn("Test Description");
            
            when(productDataNode.has("price")).thenReturn(true);
            when(productDataNode.path("price")).thenReturn(priceNode);
            when(priceNode.asText()).thenReturn("10.99");
            
            when(productDataNode.has("cookingTime")).thenReturn(true);
            when(productDataNode.path("cookingTime")).thenReturn(cookingTimeNode);
            when(cookingTimeNode.asInt()).thenReturn(15);

            // Act
            StandardProductMessage result = consumer.deserializeMessage(message, StandardProductMessage.class);

            // Assert
            assertNotNull(result);
            assertEquals("123", result.getMessageId());
            assertEquals("PRODUCT_CREATED", result.getEventType());
            assertEquals("p123", result.getProductId());
            assertEquals("Test Product", result.getName());
            assertEquals("FOOD", result.getCategory());
            assertEquals("Test Description", result.getDescription());
            assertEquals(new BigDecimal("10.99"), result.getPrice());
            assertEquals(15, result.getCookingTime());
        }

        @Test
        @DisplayName("Should throw exception when deserialization fails")
        void deserializeMessage_shouldThrowException_whenDeserializationFails() throws JsonProcessingException {
            // Arrange
            String messageBody = "{invalid-json}";
            Message message = Message.builder().body(messageBody).build();
            
            // Create custom exceptions that don't log stack traces in tests
            JsonProcessingException testException = new JsonProcessingException("Invalid JSON") {};
            
            // Mock both methods to throw exceptions
            when(objectMapper.readValue(eq(messageBody), eq(StandardProductMessage.class)))
                    .thenThrow(testException);
            when(objectMapper.readTree(messageBody)).thenThrow(testException);

            // Act & Assert
            Exception exception = assertThrows(RuntimeException.class, () ->
                    consumer.deserializeMessage(message, StandardProductMessage.class));
            
            assertEquals("Failed to deserialize message", exception.getMessage());
        }
    }
}
