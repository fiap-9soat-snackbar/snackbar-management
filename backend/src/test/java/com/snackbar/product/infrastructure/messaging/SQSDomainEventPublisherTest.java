package com.snackbar.product.infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.snackbar.infrastructure.messaging.sqs.model.StandardProductMessage;
import com.snackbar.infrastructure.messaging.sqs.producer.SQSMessageProducer;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.event.ProductCreatedEvent;

@ExtendWith(MockitoExtension.class)
class SQSDomainEventPublisherTest {

    @Mock
    private ProductMessageMapper messageMapper;
    
    @Mock
    private SQSMessageProducer messageProducer;
    
    private SQSDomainEventPublisher publisher;
    private final String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue";
    
    @BeforeEach
    void setUp() {
        publisher = new SQSDomainEventPublisher(messageMapper, messageProducer);
        
        // Set the queue URL using reflection
        ReflectionTestUtils.setField(publisher, "queueUrl", queueUrl);
    }
    
    @Test
    void shouldPublishEventSuccessfully() {
        // Arrange
        Product product = new Product("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5);
        ProductCreatedEvent event = new ProductCreatedEvent(product);
        
        StandardProductMessage message = new StandardProductMessage(StandardProductMessage.EVENT_TYPE_CREATED);
        message.setProductId("1");
        message.setName("Test Product");
        
        when(messageMapper.toMessage(event)).thenReturn(message);
        
        // Act
        publisher.publish(event);
        
        // Verify
        verify(messageMapper).toMessage(event);
        verify(messageProducer).sendMessage(eq(queueUrl), eq(message));
    }
    
    @Test
    void shouldThrowExceptionWhenQueueUrlIsNull() {
        // Arrange
        // Set null queue URL
        ReflectionTestUtils.setField(publisher, "queueUrl", null);
        
        Product product = new Product("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5);
        ProductCreatedEvent event = new ProductCreatedEvent(product);
        
        StandardProductMessage message = new StandardProductMessage(StandardProductMessage.EVENT_TYPE_CREATED);
        when(messageMapper.toMessage(event)).thenReturn(message);
        
        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            publisher.publish(event);
        });
        
        assertEquals("SQS queue URL is not configured", exception.getMessage());
    }
}
