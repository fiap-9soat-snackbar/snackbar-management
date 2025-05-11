package com.snackbar.product.infrastructure.messaging.sqs.producer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

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

    @Nested
    @DisplayName("Product Created Event Tests")
    class ProductCreatedEventTests {
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
        @DisplayName("Should handle ProductCreatedEvent with test exception")
        void handleProductCreatedEvent_TestException() {
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
        @DisplayName("Should handle ProductCreatedEvent with non-test exception")
        void handleProductCreatedEvent_NonTestException() {
            // Given
            ProductCreatedEvent event = new ProductCreatedEvent(product);
            when(messageMapper.toMessage(event)).thenReturn(message);
            doThrow(new RuntimeException("Database error")).when(messageProducer).sendMessage(eq(queueUrl), any());
            
            // Replace logger with mock to verify error logging
            Logger mockLogger = mock(Logger.class);
            org.springframework.test.util.ReflectionTestUtils.setField(producer, "logger", mockLogger);

            // When
            assertDoesNotThrow(() -> producer.handleProductCreatedEvent(event));

            // Then
            verify(messageMapper, times(1)).toMessage(event);
            verify(messageProducer, times(1)).sendMessage(eq(queueUrl), eq(message));
            verify(mockLogger).error(eq("Failed to send ProductCreatedEvent to SQS for product ID: {}"), 
                    eq("1"), any(RuntimeException.class));
        }
        
        @Test
        @DisplayName("Should handle ProductCreatedEvent with mapper exception")
        void handleProductCreatedEvent_MapperException() {
            // Given
            ProductCreatedEvent event = new ProductCreatedEvent(product);
            when(messageMapper.toMessage(event)).thenThrow(new RuntimeException("Mapping error"));
            
            // Replace logger with mock to verify error logging
            Logger mockLogger = mock(Logger.class);
            org.springframework.test.util.ReflectionTestUtils.setField(producer, "logger", mockLogger);

            // When
            assertDoesNotThrow(() -> producer.handleProductCreatedEvent(event));

            // Then
            verify(messageMapper, times(1)).toMessage(event);
            verify(messageProducer, times(0)).sendMessage(any(), any());
            verify(mockLogger).error(eq("Failed to send ProductCreatedEvent to SQS for product ID: {}"), 
                    eq("1"), any(RuntimeException.class));
        }
    }

    @Nested
    @DisplayName("Product Updated Event Tests")
    class ProductUpdatedEventTests {
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
        @DisplayName("Should handle ProductUpdatedEvent with test exception")
        void handleProductUpdatedEvent_TestException() {
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
        @DisplayName("Should handle ProductUpdatedEvent with non-test exception")
        void handleProductUpdatedEvent_NonTestException() {
            // Given
            ProductUpdatedEvent event = new ProductUpdatedEvent(product);
            when(messageMapper.toMessage(event)).thenReturn(message);
            doThrow(new RuntimeException("Database error")).when(messageProducer).sendMessage(eq(queueUrl), any());
            
            // Replace logger with mock to verify error logging
            Logger mockLogger = mock(Logger.class);
            org.springframework.test.util.ReflectionTestUtils.setField(producer, "logger", mockLogger);

            // When
            assertDoesNotThrow(() -> producer.handleProductUpdatedEvent(event));

            // Then
            verify(messageMapper, times(1)).toMessage(event);
            verify(messageProducer, times(1)).sendMessage(eq(queueUrl), eq(message));
            verify(mockLogger).error(eq("Failed to send ProductUpdatedEvent to SQS for product ID: {}"), 
                    eq("1"), any(RuntimeException.class));
        }
        
        @Test
        @DisplayName("Should handle ProductUpdatedEvent with mapper exception")
        void handleProductUpdatedEvent_MapperException() {
            // Given
            ProductUpdatedEvent event = new ProductUpdatedEvent(product);
            when(messageMapper.toMessage(event)).thenThrow(new RuntimeException("Mapping error"));
            
            // Replace logger with mock to verify error logging
            Logger mockLogger = mock(Logger.class);
            org.springframework.test.util.ReflectionTestUtils.setField(producer, "logger", mockLogger);

            // When
            assertDoesNotThrow(() -> producer.handleProductUpdatedEvent(event));

            // Then
            verify(messageMapper, times(1)).toMessage(event);
            verify(messageProducer, times(0)).sendMessage(any(), any());
            verify(mockLogger).error(eq("Failed to send ProductUpdatedEvent to SQS for product ID: {}"), 
                    eq("1"), any(RuntimeException.class));
        }
    }

    @Nested
    @DisplayName("Product Deleted Event Tests")
    class ProductDeletedEventTests {
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
        @DisplayName("Should handle ProductDeletedEvent with test exception")
        void handleProductDeletedEvent_TestException() {
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
        
        @Test
        @DisplayName("Should handle ProductDeletedEvent with non-test exception")
        void handleProductDeletedEvent_NonTestException() {
            // Given
            ProductDeletedEvent event = new ProductDeletedEvent("1");
            when(messageMapper.toMessage(event)).thenReturn(message);
            doThrow(new RuntimeException("Database error")).when(messageProducer).sendMessage(eq(queueUrl), any());
            
            // Replace logger with mock to verify error logging
            Logger mockLogger = mock(Logger.class);
            org.springframework.test.util.ReflectionTestUtils.setField(producer, "logger", mockLogger);

            // When
            assertDoesNotThrow(() -> producer.handleProductDeletedEvent(event));

            // Then
            verify(messageMapper, times(1)).toMessage(event);
            verify(messageProducer, times(1)).sendMessage(eq(queueUrl), eq(message));
            verify(mockLogger).error(eq("Failed to send ProductDeletedEvent to SQS for product ID: {}"), 
                    eq("1"), any(RuntimeException.class));
        }
        
        @Test
        @DisplayName("Should handle ProductDeletedEvent with mapper exception")
        void handleProductDeletedEvent_MapperException() {
            // Given
            ProductDeletedEvent event = new ProductDeletedEvent("1");
            when(messageMapper.toMessage(event)).thenThrow(new RuntimeException("Mapping error"));
            
            // Replace logger with mock to verify error logging
            Logger mockLogger = mock(Logger.class);
            org.springframework.test.util.ReflectionTestUtils.setField(producer, "logger", mockLogger);

            // When
            assertDoesNotThrow(() -> producer.handleProductDeletedEvent(event));

            // Then
            verify(messageMapper, times(1)).toMessage(event);
            verify(messageProducer, times(0)).sendMessage(any(), any());
            verify(mockLogger).error(eq("Failed to send ProductDeletedEvent to SQS for product ID: {}"), 
                    eq("1"), any(RuntimeException.class));
        }
    }
}
