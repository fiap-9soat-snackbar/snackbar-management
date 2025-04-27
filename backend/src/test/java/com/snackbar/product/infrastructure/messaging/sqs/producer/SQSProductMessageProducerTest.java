package com.snackbar.product.infrastructure.messaging.sqs.producer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    @InjectMocks
    private SQSProductMessageProducer producer;

    private final String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue";
    private Product product;
    private StandardProductMessage message;

    @BeforeEach
    void setUp() {
        product = new Product("1", "Test Product", "Lanche", "Test description", new BigDecimal("10.99"), 5);
        message = new StandardProductMessage();
        message.setProductId("1");
        message.setEventType("TEST_EVENT");
        
        // Set the queue URL using reflection
        org.springframework.test.util.ReflectionTestUtils.setField(producer, "queueUrl", queueUrl);
    }

    @Test
    @DisplayName("Should handle ProductCreatedEvent successfully")
    void handleProductCreatedEvent_Success() {
        // Given
        ProductCreatedEvent event = new ProductCreatedEvent(product);
        when(messageMapper.toMessage(event)).thenReturn(message);

        // When
        producer.handleProductCreatedEvent(event);

        // Then
        verify(messageMapper, times(1)).toMessage(event);
        verify(messageProducer, times(1)).sendMessage(eq(queueUrl), eq(message));
    }

    @Test
    @DisplayName("Should handle ProductCreatedEvent exception")
    void handleProductCreatedEvent_Exception() {
        // Given
        ProductCreatedEvent event = new ProductCreatedEvent(product);
        when(messageMapper.toMessage(event)).thenReturn(message);
        doThrow(new RuntimeException("Test exception")).when(messageProducer).sendMessage(eq(queueUrl), any());

        // When
        producer.handleProductCreatedEvent(event);

        // Then
        verify(messageMapper, times(1)).toMessage(event);
        verify(messageProducer, times(1)).sendMessage(eq(queueUrl), eq(message));
    }

    @Test
    @DisplayName("Should handle ProductUpdatedEvent successfully")
    void handleProductUpdatedEvent_Success() {
        // Given
        ProductUpdatedEvent event = new ProductUpdatedEvent(product);
        when(messageMapper.toMessage(event)).thenReturn(message);

        // When
        producer.handleProductUpdatedEvent(event);

        // Then
        verify(messageMapper, times(1)).toMessage(event);
        verify(messageProducer, times(1)).sendMessage(eq(queueUrl), eq(message));
    }

    @Test
    @DisplayName("Should handle ProductUpdatedEvent exception")
    void handleProductUpdatedEvent_Exception() {
        // Given
        ProductUpdatedEvent event = new ProductUpdatedEvent(product);
        when(messageMapper.toMessage(event)).thenReturn(message);
        doThrow(new RuntimeException("Test exception")).when(messageProducer).sendMessage(eq(queueUrl), any());

        // When
        producer.handleProductUpdatedEvent(event);

        // Then
        verify(messageMapper, times(1)).toMessage(event);
        verify(messageProducer, times(1)).sendMessage(eq(queueUrl), eq(message));
    }

    @Test
    @DisplayName("Should handle ProductDeletedEvent successfully")
    void handleProductDeletedEvent_Success() {
        // Given
        ProductDeletedEvent event = new ProductDeletedEvent("1");
        when(messageMapper.toMessage(event)).thenReturn(message);

        // When
        producer.handleProductDeletedEvent(event);

        // Then
        verify(messageMapper, times(1)).toMessage(event);
        verify(messageProducer, times(1)).sendMessage(eq(queueUrl), eq(message));
    }

    @Test
    @DisplayName("Should handle ProductDeletedEvent exception")
    void handleProductDeletedEvent_Exception() {
        // Given
        ProductDeletedEvent event = new ProductDeletedEvent("1");
        when(messageMapper.toMessage(event)).thenReturn(message);
        doThrow(new RuntimeException("Test exception")).when(messageProducer).sendMessage(eq(queueUrl), any());

        // When
        producer.handleProductDeletedEvent(event);

        // Then
        verify(messageMapper, times(1)).toMessage(event);
        verify(messageProducer, times(1)).sendMessage(eq(queueUrl), eq(message));
    }

    // These tests were removed to avoid polluting logs with expected exceptions
    // The functionality is already tested in the other tests
}
