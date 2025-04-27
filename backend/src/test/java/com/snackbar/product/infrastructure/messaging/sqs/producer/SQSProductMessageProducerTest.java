package com.snackbar.product.infrastructure.messaging.sqs.producer;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.snackbar.infrastructure.messaging.sqs.producer.SQSMessageProducer;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.event.ProductCreatedEvent;
import com.snackbar.product.domain.event.ProductDeletedEvent;
import com.snackbar.product.domain.event.ProductUpdatedEvent;
import com.snackbar.product.infrastructure.messaging.mapper.ProductMessageMapper;
import com.snackbar.product.infrastructure.messaging.sqs.model.StandardProductMessage;

@ExtendWith(MockitoExtension.class)
class SQSProductMessageProducerTest {

    @Mock
    private SQSMessageProducer messageProducer;
    
    @Mock
    private ProductMessageMapper messageMapper;
    
    private SQSProductMessageProducer producer;
    private final String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue";
    
    @BeforeEach
    void setUp() {
        producer = new SQSProductMessageProducer(messageProducer, messageMapper, queueUrl);
    }
    
    @Test
    void shouldHandleProductCreatedEvent() {
        // Arrange
        Product product = new Product("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5);
        ProductCreatedEvent event = new ProductCreatedEvent(product);
        
        StandardProductMessage message = new StandardProductMessage(StandardProductMessage.EVENT_TYPE_CREATED);
        message.setProductId("1");
        
        when(messageMapper.toMessage(event)).thenReturn(message);
        
        // Act
        producer.handleProductCreatedEvent(event);
        
        // Verify
        verify(messageMapper).toMessage(event);
        verify(messageProducer).sendMessage(eq(queueUrl), eq(message));
    }
    
    @Test
    void shouldHandleProductUpdatedEvent() {
        // Arrange
        Product product = new Product("1", "Updated Product", "Lanche", "Updated description", BigDecimal.valueOf(12.99), 7);
        ProductUpdatedEvent event = new ProductUpdatedEvent(product);
        
        StandardProductMessage message = new StandardProductMessage(StandardProductMessage.EVENT_TYPE_UPDATED);
        message.setProductId("1");
        
        when(messageMapper.toMessage(event)).thenReturn(message);
        
        // Act
        producer.handleProductUpdatedEvent(event);
        
        // Verify
        verify(messageMapper).toMessage(event);
        verify(messageProducer).sendMessage(eq(queueUrl), eq(message));
    }
    
    @Test
    void shouldHandleProductDeletedEvent() {
        // Arrange
        String productId = "1";
        ProductDeletedEvent event = new ProductDeletedEvent(productId);
        
        StandardProductMessage message = new StandardProductMessage(StandardProductMessage.EVENT_TYPE_DELETED);
        message.setProductId("1");
        
        when(messageMapper.toMessage(event)).thenReturn(message);
        
        // Act
        producer.handleProductDeletedEvent(event);
        
        // Verify
        verify(messageMapper).toMessage(event);
        verify(messageProducer).sendMessage(eq(queueUrl), eq(message));
    }
    
    @Test
    void shouldHandleExceptionWhenSendingProductCreatedEvent() {
        // Arrange
        Product product = new Product("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5);
        ProductCreatedEvent event = new ProductCreatedEvent(product);
        
        StandardProductMessage message = new StandardProductMessage(StandardProductMessage.EVENT_TYPE_CREATED);
        message.setProductId("1");
        
        when(messageMapper.toMessage(event)).thenReturn(message);
        doThrow(new RuntimeException("Test exception")).when(messageProducer).sendMessage(eq(queueUrl), eq(message));
        
        // Act
        producer.handleProductCreatedEvent(event);
        
        // Verify
        verify(messageMapper).toMessage(event);
        verify(messageProducer).sendMessage(eq(queueUrl), eq(message));
        // No exception should be thrown outside the handler
    }
    
    @Test
    void shouldHandleExceptionWhenSendingProductUpdatedEvent() {
        // Arrange
        Product product = new Product("1", "Updated Product", "Lanche", "Updated description", BigDecimal.valueOf(12.99), 7);
        ProductUpdatedEvent event = new ProductUpdatedEvent(product);
        
        StandardProductMessage message = new StandardProductMessage(StandardProductMessage.EVENT_TYPE_UPDATED);
        message.setProductId("1");
        
        when(messageMapper.toMessage(event)).thenReturn(message);
        doThrow(new RuntimeException("Test exception")).when(messageProducer).sendMessage(eq(queueUrl), eq(message));
        
        // Act
        producer.handleProductUpdatedEvent(event);
        
        // Verify
        verify(messageMapper).toMessage(event);
        verify(messageProducer).sendMessage(eq(queueUrl), eq(message));
        // No exception should be thrown outside the handler
    }
    
    @Test
    void shouldHandleExceptionWhenSendingProductDeletedEvent() {
        // Arrange
        String productId = "1";
        ProductDeletedEvent event = new ProductDeletedEvent(productId);
        
        StandardProductMessage message = new StandardProductMessage(StandardProductMessage.EVENT_TYPE_DELETED);
        message.setProductId("1");
        
        when(messageMapper.toMessage(event)).thenReturn(message);
        doThrow(new RuntimeException("Test exception")).when(messageProducer).sendMessage(eq(queueUrl), eq(message));
        
        // Act
        producer.handleProductDeletedEvent(event);
        
        // Verify
        verify(messageMapper).toMessage(event);
        verify(messageProducer).sendMessage(eq(queueUrl), eq(message));
        // No exception should be thrown outside the handler
    }
}
