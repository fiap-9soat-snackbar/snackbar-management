package com.snackbar.product.infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.event.ProductCreatedEvent;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@ExtendWith(MockitoExtension.class)
class SQSDomainEventPublisherTest {

    @Mock
    private ProductMessageMapper messageMapper;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @Mock
    private SqsClient sqsClient;
    
    private SQSDomainEventPublisher publisher;
    private final String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue";
    
    @BeforeEach
    void setUp() {
        publisher = new SQSDomainEventPublisher(messageMapper, objectMapper, sqsClient, queueUrl);
    }
    
    @Test
    void shouldPublishEventSuccessfully() throws Exception {
        // Arrange
        Product product = new Product("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5);
        ProductCreatedEvent event = new ProductCreatedEvent(product);
        
        ProductMessage message = new ProductMessage("msg-id", "PRODUCT_CREATED", Instant.now(), 
                new ProductMessage.ProductData("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5));
        
        when(messageMapper.toMessage(event)).thenReturn(message);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"messageId\":\"msg-id\"}");
        when(sqsClient.sendMessage(any(SendMessageRequest.class))).thenReturn(SendMessageResponse.builder().messageId("msg-id").build());
        
        // Act
        publisher.publish(event);
        
        // Verify
        verify(messageMapper).toMessage(event);
        verify(objectMapper).writeValueAsString(any());
        verify(sqsClient).sendMessage(any(SendMessageRequest.class));
    }
    
    @Test
    void shouldThrowExceptionWhenJsonProcessingFails() throws Exception {
        // Arrange
        Product product = new Product("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5);
        ProductCreatedEvent event = new ProductCreatedEvent(product);
        
        ProductMessage message = new ProductMessage("msg-id", "PRODUCT_CREATED", Instant.now(), 
                new ProductMessage.ProductData("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5));
        
        when(messageMapper.toMessage(event)).thenReturn(message);
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Test error") {});
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> publisher.publish(event));
        assertEquals("Failed to serialize event to JSON", exception.getMessage());
        
        // Verify
        verify(messageMapper).toMessage(event);
        verify(objectMapper).writeValueAsString(any());
        verify(sqsClient, never()).sendMessage(any(SendMessageRequest.class));
    }
    
    @Test
    void shouldThrowExceptionWhenMessageMapperFails() throws JsonProcessingException {
        // Arrange
        Product product = new Product("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5);
        ProductCreatedEvent event = new ProductCreatedEvent(product);
        
        when(messageMapper.toMessage(event)).thenThrow(new IllegalArgumentException("Test error"));
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> publisher.publish(event));
        assertEquals("Failed to publish event", exception.getMessage());
        
        // Verify
        verify(messageMapper).toMessage(event);
        verify(objectMapper, never()).writeValueAsString(any());
        verify(sqsClient, never()).sendMessage(any(SendMessageRequest.class));
    }
    
    @Test
    void shouldThrowExceptionWhenSqsClientFails() throws JsonProcessingException {
        // Arrange
        Product product = new Product("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5);
        ProductCreatedEvent event = new ProductCreatedEvent(product);
        
        ProductMessage message = new ProductMessage("msg-id", "PRODUCT_CREATED", Instant.now(), 
                new ProductMessage.ProductData("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5));
        
        when(messageMapper.toMessage(event)).thenReturn(message);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"messageId\":\"msg-id\"}");
        when(sqsClient.sendMessage(any(SendMessageRequest.class))).thenThrow(new RuntimeException("SQS error"));
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> publisher.publish(event));
        assertEquals("Failed to publish event", exception.getMessage());
        
        // Verify
        verify(messageMapper).toMessage(event);
        verify(objectMapper).writeValueAsString(any());
        verify(sqsClient).sendMessage(any(SendMessageRequest.class));
    }
}