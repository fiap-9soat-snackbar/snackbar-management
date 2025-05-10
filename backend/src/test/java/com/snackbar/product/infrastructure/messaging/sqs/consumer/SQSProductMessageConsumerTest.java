package com.snackbar.product.infrastructure.messaging.sqs.consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import com.snackbar.infrastructure.messaging.sqs.consumer.SQSMessageConsumer;
import com.snackbar.product.application.ports.in.CreateProductInputPort;
import com.snackbar.product.application.ports.in.DeleteProductByIdInputPort;
import com.snackbar.product.application.ports.in.UpdateProductByIdInputPort;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.exceptions.ProductNotFoundException;
import com.snackbar.product.infrastructure.messaging.mapper.ProductMessageMapper;
import com.snackbar.product.infrastructure.messaging.sqs.model.StandardProductMessage;

import software.amazon.awssdk.services.sqs.model.Message;

@ExtendWith(MockitoExtension.class)
class SQSProductMessageConsumerTest {

    @Mock
    private SQSMessageConsumer messageConsumer;

    @Mock
    private ProductMessageMapper messageMapper;

    @Mock
    private CreateProductInputPort createProductUseCase;

    @Mock
    private UpdateProductByIdInputPort updateProductUseCase;

    @Mock
    private DeleteProductByIdInputPort deleteProductUseCase;
    
    @Mock
    private Logger mockLogger;

    @InjectMocks
    private SQSProductMessageConsumer consumer;

    private final String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue";
    private Message message;
    private StandardProductMessage productMessage;
    private Product product;

    @BeforeEach
    void setUp() {
        // Set up test data
        message = Message.builder()
                .body("{\"productId\":\"1\",\"eventType\":\"PRODUCT_CREATED\"}")
                .receiptHandle("receipt-handle")
                .build();
        
        productMessage = new StandardProductMessage();
        productMessage.setProductId("1");
        
        product = new Product("1", "Test Product", "Lanche", "Test description", new BigDecimal("10.99"), 5);
        
        // Set up configuration using reflection
        ReflectionTestUtils.setField(consumer, "queueUrl", queueUrl);
        ReflectionTestUtils.setField(consumer, "pollingEnabled", true);
        ReflectionTestUtils.setField(consumer, "pollingDelayMs", 1000L);
        ReflectionTestUtils.setField(consumer, "maxMessages", 10);
        ReflectionTestUtils.setField(consumer, "waitTimeSeconds", 5);
        
        // Set mock logger to avoid log pollution during tests
        consumer.setLogger(mockLogger);
    }

    @Test
    @DisplayName("Should not poll when polling is disabled")
    void pollMessages_ShouldNotPollWhenDisabled() {
        // Given
        ReflectionTestUtils.setField(consumer, "pollingEnabled", false);

        // When
        consumer.pollMessages();

        // Then
        verify(messageConsumer, never()).receiveMessages(anyString(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should not process when no messages are received")
    void pollMessages_ShouldNotProcessWhenNoMessages() {
        // Given
        when(messageConsumer.receiveMessages(queueUrl, 10, 5)).thenReturn(Collections.emptyList());

        // When
        consumer.pollMessages();

        // Then
        verify(messageConsumer, times(1)).receiveMessages(queueUrl, 10, 5);
        verify(messageConsumer, never()).deserializeMessage(any(), any());
    }

    @Test
    @DisplayName("Should process PRODUCT_CREATED message")
    void pollMessages_ShouldProcessProductCreatedMessage() {
        // Given
        productMessage.setEventType(StandardProductMessage.EVENT_TYPE_CREATED);
        
        when(messageConsumer.receiveMessages(queueUrl, 10, 5)).thenReturn(List.of(message));
        when(messageConsumer.deserializeMessage(message, StandardProductMessage.class)).thenReturn(productMessage);
        when(messageMapper.toDomainObject(productMessage)).thenReturn(product);

        // When
        consumer.pollMessages();

        // Then
        verify(messageConsumer, times(1)).receiveMessages(queueUrl, 10, 5);
        verify(messageConsumer, times(1)).deserializeMessage(message, StandardProductMessage.class);
        verify(messageMapper, times(1)).toDomainObject(productMessage);
        verify(createProductUseCase, times(1)).createProduct(product);
        verify(messageConsumer, times(1)).deleteMessage(queueUrl, "receipt-handle");
    }

    @Test
    @DisplayName("Should process PRODUCT_UPDATED message")
    void pollMessages_ShouldProcessProductUpdatedMessage() {
        // Given
        productMessage.setEventType(StandardProductMessage.EVENT_TYPE_UPDATED);
        
        when(messageConsumer.receiveMessages(queueUrl, 10, 5)).thenReturn(List.of(message));
        when(messageConsumer.deserializeMessage(message, StandardProductMessage.class)).thenReturn(productMessage);
        when(messageMapper.toDomainObject(productMessage)).thenReturn(product);

        // When
        consumer.pollMessages();

        // Then
        verify(messageConsumer, times(1)).receiveMessages(queueUrl, 10, 5);
        verify(messageConsumer, times(1)).deserializeMessage(message, StandardProductMessage.class);
        verify(messageMapper, times(1)).toDomainObject(productMessage);
        verify(updateProductUseCase, times(1)).updateProductById("1", product);
        verify(messageConsumer, times(1)).deleteMessage(queueUrl, "receipt-handle");
    }

    @Test
    @DisplayName("Should process PRODUCT_DELETED message")
    void pollMessages_ShouldProcessProductDeletedMessage() {
        // Given
        productMessage.setEventType(StandardProductMessage.EVENT_TYPE_DELETED);
        
        when(messageConsumer.receiveMessages(queueUrl, 10, 5)).thenReturn(List.of(message));
        when(messageConsumer.deserializeMessage(message, StandardProductMessage.class)).thenReturn(productMessage);

        // When
        consumer.pollMessages();

        // Then
        verify(messageConsumer, times(1)).receiveMessages(queueUrl, 10, 5);
        verify(messageConsumer, times(1)).deserializeMessage(message, StandardProductMessage.class);
        verify(messageMapper, never()).toDomainObject(productMessage); // No mapping needed for delete
        verify(deleteProductUseCase, times(1)).deleteProductById("1");
        verify(messageConsumer, times(1)).deleteMessage(queueUrl, "receipt-handle");
    }

    @Test
    @DisplayName("Should handle unknown event type")
    void pollMessages_ShouldHandleUnknownEventType() {
        // Given
        productMessage.setEventType("UNKNOWN_EVENT");
        
        when(messageConsumer.receiveMessages(queueUrl, 10, 5)).thenReturn(List.of(message));
        when(messageConsumer.deserializeMessage(message, StandardProductMessage.class)).thenReturn(productMessage);

        // When
        consumer.pollMessages();

        // Then
        verify(messageConsumer, times(1)).receiveMessages(queueUrl, 10, 5);
        verify(messageConsumer, times(1)).deserializeMessage(message, StandardProductMessage.class);
        verify(messageMapper, never()).toDomainObject(any());
        verify(createProductUseCase, never()).createProduct(any());
        verify(updateProductUseCase, never()).updateProductById(anyString(), any());
        verify(deleteProductUseCase, never()).deleteProductById(anyString());
        verify(messageConsumer, times(1)).deleteMessage(queueUrl, "receipt-handle");
    }

    @Test
    @DisplayName("Should handle multiple messages")
    void pollMessages_ShouldHandleMultipleMessages() {
        // Given
        Message message1 = Message.builder()
                .body("{\"productId\":\"1\",\"eventType\":\"PRODUCT_CREATED\"}")
                .receiptHandle("receipt-handle-1")
                .build();
        
        Message message2 = Message.builder()
                .body("{\"productId\":\"2\",\"eventType\":\"PRODUCT_UPDATED\"}")
                .receiptHandle("receipt-handle-2")
                .build();
        
        StandardProductMessage productMessage1 = new StandardProductMessage();
        productMessage1.setProductId("1");
        productMessage1.setEventType(StandardProductMessage.EVENT_TYPE_CREATED);
        
        StandardProductMessage productMessage2 = new StandardProductMessage();
        productMessage2.setProductId("2");
        productMessage2.setEventType(StandardProductMessage.EVENT_TYPE_UPDATED);
        
        Product product1 = new Product("1", "Product 1", "Lanche", "Description 1", new BigDecimal("10.99"), 5);
        Product product2 = new Product("2", "Product 2", "Bebida", "Description 2", new BigDecimal("5.99"), 0);
        
        when(messageConsumer.receiveMessages(queueUrl, 10, 5)).thenReturn(Arrays.asList(message1, message2));
        when(messageConsumer.deserializeMessage(message1, StandardProductMessage.class)).thenReturn(productMessage1);
        when(messageConsumer.deserializeMessage(message2, StandardProductMessage.class)).thenReturn(productMessage2);
        when(messageMapper.toDomainObject(productMessage1)).thenReturn(product1);
        when(messageMapper.toDomainObject(productMessage2)).thenReturn(product2);

        // When
        consumer.pollMessages();

        // Then
        verify(messageConsumer, times(1)).receiveMessages(queueUrl, 10, 5);
        verify(messageConsumer, times(1)).deserializeMessage(message1, StandardProductMessage.class);
        verify(messageConsumer, times(1)).deserializeMessage(message2, StandardProductMessage.class);
        verify(messageMapper, times(1)).toDomainObject(productMessage1);
        verify(messageMapper, times(1)).toDomainObject(productMessage2);
        verify(createProductUseCase, times(1)).createProduct(product1);
        verify(updateProductUseCase, times(1)).updateProductById("2", product2);
        verify(messageConsumer, times(1)).deleteMessage(queueUrl, "receipt-handle-1");
        verify(messageConsumer, times(1)).deleteMessage(queueUrl, "receipt-handle-2");
    }

    @Test
    @DisplayName("Should handle exception in message processing")
    void pollMessages_ShouldHandleExceptionInMessageProcessing() {
        // Given
        productMessage.setEventType(StandardProductMessage.EVENT_TYPE_CREATED);
        
        when(messageConsumer.receiveMessages(queueUrl, 10, 5)).thenReturn(List.of(message));
        when(messageConsumer.deserializeMessage(message, StandardProductMessage.class)).thenReturn(productMessage);
        when(messageMapper.toDomainObject(productMessage)).thenReturn(product);
        doThrow(new RuntimeException("Test exception")).when(createProductUseCase).createProduct(product);

        // When
        consumer.pollMessages();

        // Then
        verify(messageConsumer, times(1)).receiveMessages(queueUrl, 10, 5);
        verify(messageConsumer, times(1)).deserializeMessage(message, StandardProductMessage.class);
        verify(messageMapper, times(1)).toDomainObject(productMessage);
        verify(createProductUseCase, times(1)).createProduct(product);
        verify(messageConsumer, never()).deleteMessage(anyString(), anyString()); // Message not deleted due to exception
    }

    @Test
    @DisplayName("Should handle exception in polling")
    void pollMessages_ShouldHandleExceptionInPolling() {
        // Given
        when(messageConsumer.receiveMessages(queueUrl, 10, 5)).thenThrow(new RuntimeException("Test exception"));

        // When
        consumer.pollMessages();

        // Then
        verify(messageConsumer, times(1)).receiveMessages(queueUrl, 10, 5);
        verify(messageConsumer, never()).deserializeMessage(any(), any());
        verify(messageConsumer, never()).deleteMessage(anyString(), anyString());
    }

    @Test
    @DisplayName("Should handle exception in message deserialization")
    void pollMessages_ShouldHandleExceptionInMessageDeserialization() {
        // Given
        when(messageConsumer.receiveMessages(queueUrl, 10, 5)).thenReturn(List.of(message));
        when(messageConsumer.deserializeMessage(message, StandardProductMessage.class))
                .thenThrow(new RuntimeException("Test exception"));

        // When
        consumer.pollMessages();

        // Then
        verify(messageConsumer, times(1)).receiveMessages(queueUrl, 10, 5);
        verify(messageConsumer, times(1)).deserializeMessage(message, StandardProductMessage.class);
        verify(messageConsumer, never()).deleteMessage(anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should test setLogger method")
    void setLogger_ShouldSetLoggerCorrectly() {
        // Given
        Logger newLogger = mock(Logger.class);
        
        // When
        consumer.setLogger(newLogger);
        
        // Then
        // Verification is implicit - no exception means success
        assertDoesNotThrow(() -> consumer.pollMessages());
    }

    @Test
    @DisplayName("Should handle exception in handleProductCreated")
    void handleProductCreated_ShouldHandleException() {
        // Given
        productMessage.setEventType(StandardProductMessage.EVENT_TYPE_CREATED);
        
        when(messageConsumer.receiveMessages(queueUrl, 10, 5)).thenReturn(List.of(message));
        when(messageConsumer.deserializeMessage(message, StandardProductMessage.class)).thenReturn(productMessage);
        when(messageMapper.toDomainObject(productMessage)).thenReturn(product);
        doThrow(new RuntimeException("Error creating product")).when(createProductUseCase).createProduct(product);

        // When
        consumer.pollMessages();

        // Then
        verify(messageConsumer, times(1)).receiveMessages(queueUrl, 10, 5);
        verify(messageConsumer, times(1)).deserializeMessage(message, StandardProductMessage.class);
        verify(messageMapper, times(1)).toDomainObject(productMessage);
        verify(createProductUseCase, times(1)).createProduct(product);
        verify(messageConsumer, never()).deleteMessage(queueUrl, "receipt-handle");
        verify(mockLogger).error(eq("Failed to create product from message: {}"), eq("1"), any(RuntimeException.class));
    }

    @Test
    @DisplayName("Should handle exception in handleProductUpdated")
    void handleProductUpdated_ShouldHandleException() {
        // Given
        productMessage.setEventType(StandardProductMessage.EVENT_TYPE_UPDATED);
        
        when(messageConsumer.receiveMessages(queueUrl, 10, 5)).thenReturn(List.of(message));
        when(messageConsumer.deserializeMessage(message, StandardProductMessage.class)).thenReturn(productMessage);
        when(messageMapper.toDomainObject(productMessage)).thenReturn(product);
        doThrow(new RuntimeException("Error updating product")).when(updateProductUseCase).updateProductById(anyString(), any(Product.class));

        // When
        consumer.pollMessages();

        // Then
        verify(messageConsumer, times(1)).receiveMessages(queueUrl, 10, 5);
        verify(messageConsumer, times(1)).deserializeMessage(message, StandardProductMessage.class);
        verify(messageMapper, times(1)).toDomainObject(productMessage);
        verify(updateProductUseCase, times(1)).updateProductById("1", product);
        verify(messageConsumer, never()).deleteMessage(queueUrl, "receipt-handle");
        verify(mockLogger).error(eq("Failed to update product from message: {}"), eq("1"), any(RuntimeException.class));
    }

    @Test
    @DisplayName("Should handle exception in handleProductDeleted")
    void handleProductDeleted_ShouldHandleException() {
        // Given
        productMessage.setEventType(StandardProductMessage.EVENT_TYPE_DELETED);
        
        when(messageConsumer.receiveMessages(queueUrl, 10, 5)).thenReturn(List.of(message));
        when(messageConsumer.deserializeMessage(message, StandardProductMessage.class)).thenReturn(productMessage);
        doThrow(new ProductNotFoundException("Product not found")).when(deleteProductUseCase).deleteProductById(anyString());

        // When
        consumer.pollMessages();

        // Then
        verify(messageConsumer, times(1)).receiveMessages(queueUrl, 10, 5);
        verify(messageConsumer, times(1)).deserializeMessage(message, StandardProductMessage.class);
        verify(messageMapper, never()).toDomainObject(productMessage); // No mapping needed for delete
        verify(deleteProductUseCase, times(1)).deleteProductById("1");
        verify(messageConsumer, never()).deleteMessage(queueUrl, "receipt-handle");
        verify(mockLogger).error(eq("Failed to delete product from message: {}"), eq("1"), any(ProductNotFoundException.class));
    }
}
