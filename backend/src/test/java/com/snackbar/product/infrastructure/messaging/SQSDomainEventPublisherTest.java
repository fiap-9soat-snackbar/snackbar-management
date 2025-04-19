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

@ExtendWith(MockitoExtension.class)
class SQSDomainEventPublisherTest {

    @Mock
    private ProductMessageMapper messageMapper;
    
    @Mock
    private ObjectMapper objectMapper;
    
    private SQSDomainEventPublisher publisher;
    private final String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue";
    
    @BeforeEach
    void setUp() {
        publisher = new SQSDomainEventPublisher(messageMapper, objectMapper, queueUrl);
    }
    
    @Test
    void shouldPublishEventSuccessfully() throws Exception {
        // Arrange
        Product product = new Product("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5);
        ProductCreatedEvent event = new ProductCreatedEvent(product);
        
        ProductMessage message = new ProductMessage("msg-id", "PRODUCT_CREATED", Instant.now(), 
                new ProductMessage.ProductData("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5));
        
        when(messageMapper.toMessage(event)).thenReturn(message);
        doReturn("{\"messageId\":\"msg-id\"}").when(objectMapper).writeValueAsString(any());
        
        // Act
        publisher.publish(event);
        
        // Verify
        verify(messageMapper).toMessage(event);
        verify(objectMapper).writeValueAsString(any());
    }
    
    @Test
    void shouldThrowExceptionWhenJsonProcessingFails() throws Exception {
        // Arrange
        Product product = new Product("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5);
        ProductCreatedEvent event = new ProductCreatedEvent(product);
        
        ProductMessage message = new ProductMessage("msg-id", "PRODUCT_CREATED", Instant.now(), 
                new ProductMessage.ProductData("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5));
        
        when(messageMapper.toMessage(event)).thenReturn(message);
        doThrow(new JsonProcessingException("Test error") {}).when(objectMapper).writeValueAsString(any());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> publisher.publish(event));
        assertEquals("Failed to serialize event to JSON", exception.getMessage());
        
        // Verify
        verify(messageMapper).toMessage(event);
        verify(objectMapper).writeValueAsString(any());
    }
    
    @Test
    void shouldThrowExceptionWhenMessageMapperFails() {
        // Arrange
        Product product = new Product("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5);
        ProductCreatedEvent event = new ProductCreatedEvent(product);
        
        when(messageMapper.toMessage(event)).thenThrow(new IllegalArgumentException("Test error"));
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> publisher.publish(event));
        assertEquals("Failed to publish event", exception.getMessage());
        
        // Verify
        verify(messageMapper).toMessage(event);
        // No need to verify objectMapper as it should never be called
    }
}