package com.snackbar.product.infrastructure.messaging.sqs.consumer;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.snackbar.infrastructure.messaging.sqs.consumer.SQSMessageConsumer;
import com.snackbar.product.application.ports.in.CreateProductInputPort;
import com.snackbar.product.application.ports.in.DeleteProductByIdInputPort;
import com.snackbar.product.application.ports.in.UpdateProductByIdInputPort;
import com.snackbar.product.domain.entity.Product;
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
    
    private SQSProductMessageConsumer consumer;
    private final String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/test-queue";
    
    @BeforeEach
    void setUp() {
        consumer = new SQSProductMessageConsumer(
                messageConsumer,
                messageMapper,
                createProductUseCase,
                updateProductUseCase,
                deleteProductUseCase,
                queueUrl
        );
        
        // Set configuration properties using reflection
        ReflectionTestUtils.setField(consumer, "pollingEnabled", true);
        ReflectionTestUtils.setField(consumer, "maxMessages", 10);
        ReflectionTestUtils.setField(consumer, "waitTimeSeconds", 5);
    }
    
    @Test
    void shouldNotPollWhenPollingIsDisabled() {
        // Arrange
        ReflectionTestUtils.setField(consumer, "pollingEnabled", false);
        
        // Act
        consumer.pollMessages();
        
        // Verify
        verify(messageConsumer, never()).receiveMessages(anyString(), anyInt(), anyInt());
    }
    
    @Test
    void shouldNotProcessWhenNoMessagesReceived() {
        // Arrange
        when(messageConsumer.receiveMessages(eq(queueUrl), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        
        // Act
        consumer.pollMessages();
        
        // Verify
        verify(messageConsumer).receiveMessages(eq(queueUrl), anyInt(), anyInt());
        verify(messageConsumer, never()).deserializeMessage(any(), any());
    }
    
    @Test
    void shouldProcessCreatedEventCorrectly() {
        // Arrange
        Message sqsMessage = Message.builder()
                .messageId("test-message-id")
                .body("{\"eventType\":\"PRODUCT_CREATED\"}")
                .receiptHandle("test-receipt-handle")
                .build();
        
        StandardProductMessage productMessage = new StandardProductMessage(StandardProductMessage.EVENT_TYPE_CREATED);
        productMessage.setProductId("1");
        productMessage.setName("Test Product");
        productMessage.setCategory("Lanche");
        productMessage.setDescription("Test description");
        productMessage.setPrice(BigDecimal.valueOf(10.99));
        productMessage.setCookingTime(5);
        productMessage.setTimestamp(Instant.now());
        
        Product product = new Product("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5);
        
        when(messageConsumer.receiveMessages(eq(queueUrl), anyInt(), anyInt()))
                .thenReturn(List.of(sqsMessage));
        when(messageConsumer.deserializeMessage(sqsMessage, StandardProductMessage.class))
                .thenReturn(productMessage);
        when(messageMapper.toDomainObject(productMessage))
                .thenReturn(product);
        
        // Act
        consumer.pollMessages();
        
        // Verify
        verify(messageConsumer).receiveMessages(eq(queueUrl), anyInt(), anyInt());
        verify(messageConsumer).deserializeMessage(sqsMessage, StandardProductMessage.class);
        verify(messageMapper).toDomainObject(productMessage);
        verify(createProductUseCase).createProduct(product);
        verify(messageConsumer).deleteMessage(eq(queueUrl), eq("test-receipt-handle"));
    }
    
    @Test
    void shouldProcessUpdatedEventCorrectly() {
        // Arrange
        Message sqsMessage = Message.builder()
                .messageId("test-message-id")
                .body("{\"eventType\":\"PRODUCT_UPDATED\"}")
                .receiptHandle("test-receipt-handle")
                .build();
        
        StandardProductMessage productMessage = new StandardProductMessage(StandardProductMessage.EVENT_TYPE_UPDATED);
        productMessage.setProductId("1");
        productMessage.setName("Updated Product");
        productMessage.setCategory("Lanche");
        productMessage.setDescription("Updated description");
        productMessage.setPrice(BigDecimal.valueOf(12.99));
        productMessage.setCookingTime(7);
        productMessage.setTimestamp(Instant.now());
        
        Product product = new Product("1", "Updated Product", "Lanche", "Updated description", BigDecimal.valueOf(12.99), 7);
        
        when(messageConsumer.receiveMessages(eq(queueUrl), anyInt(), anyInt()))
                .thenReturn(List.of(sqsMessage));
        when(messageConsumer.deserializeMessage(sqsMessage, StandardProductMessage.class))
                .thenReturn(productMessage);
        when(messageMapper.toDomainObject(productMessage))
                .thenReturn(product);
        
        // Act
        consumer.pollMessages();
        
        // Verify
        verify(messageConsumer).receiveMessages(eq(queueUrl), anyInt(), anyInt());
        verify(messageConsumer).deserializeMessage(sqsMessage, StandardProductMessage.class);
        verify(messageMapper).toDomainObject(productMessage);
        verify(updateProductUseCase).updateProductById("1", product);
        verify(messageConsumer).deleteMessage(eq(queueUrl), eq("test-receipt-handle"));
    }
    
    @Test
    void shouldProcessDeletedEventCorrectly() {
        // Arrange
        Message sqsMessage = Message.builder()
                .messageId("test-message-id")
                .body("{\"eventType\":\"PRODUCT_DELETED\"}")
                .receiptHandle("test-receipt-handle")
                .build();
        
        StandardProductMessage productMessage = new StandardProductMessage(StandardProductMessage.EVENT_TYPE_DELETED);
        productMessage.setProductId("1");
        productMessage.setTimestamp(Instant.now());
        
        when(messageConsumer.receiveMessages(eq(queueUrl), anyInt(), anyInt()))
                .thenReturn(List.of(sqsMessage));
        when(messageConsumer.deserializeMessage(sqsMessage, StandardProductMessage.class))
                .thenReturn(productMessage);
        
        // Act
        consumer.pollMessages();
        
        // Verify
        verify(messageConsumer).receiveMessages(eq(queueUrl), anyInt(), anyInt());
        verify(messageConsumer).deserializeMessage(sqsMessage, StandardProductMessage.class);
        verify(messageMapper, never()).toDomainObject(any());
        verify(deleteProductUseCase).deleteProductById("1");
        verify(messageConsumer).deleteMessage(eq(queueUrl), eq("test-receipt-handle"));
    }
    
    @Test
    void shouldHandleUnknownEventTypeGracefully() {
        // Arrange
        Message sqsMessage = Message.builder()
                .messageId("test-message-id")
                .body("{\"eventType\":\"UNKNOWN_EVENT\"}")
                .receiptHandle("test-receipt-handle")
                .build();
        
        StandardProductMessage productMessage = new StandardProductMessage("UNKNOWN_EVENT");
        productMessage.setProductId("1");
        productMessage.setTimestamp(Instant.now());
        
        when(messageConsumer.receiveMessages(eq(queueUrl), anyInt(), anyInt()))
                .thenReturn(List.of(sqsMessage));
        when(messageConsumer.deserializeMessage(sqsMessage, StandardProductMessage.class))
                .thenReturn(productMessage);
        
        // Act
        consumer.pollMessages();
        
        // Verify
        verify(messageConsumer).receiveMessages(eq(queueUrl), anyInt(), anyInt());
        verify(messageConsumer).deserializeMessage(sqsMessage, StandardProductMessage.class);
        verify(messageMapper, never()).toDomainObject(any());
        verify(createProductUseCase, never()).createProduct(any());
        verify(updateProductUseCase, never()).updateProductById(anyString(), any());
        verify(deleteProductUseCase, never()).deleteProductById(anyString());
        verify(messageConsumer).deleteMessage(eq(queueUrl), eq("test-receipt-handle"));
    }
    
    @Test
    void shouldHandleExceptionDuringMessageProcessing() {
        // Arrange
        Message sqsMessage = Message.builder()
                .messageId("test-message-id")
                .body("{\"eventType\":\"PRODUCT_CREATED\"}")
                .receiptHandle("test-receipt-handle")
                .build();
        
        when(messageConsumer.receiveMessages(eq(queueUrl), anyInt(), anyInt()))
                .thenReturn(List.of(sqsMessage));
        when(messageConsumer.deserializeMessage(sqsMessage, StandardProductMessage.class))
                .thenThrow(new RuntimeException("Test exception"));
        
        // Act
        consumer.pollMessages();
        
        // Verify
        verify(messageConsumer).receiveMessages(eq(queueUrl), anyInt(), anyInt());
        verify(messageConsumer).deserializeMessage(sqsMessage, StandardProductMessage.class);
        verify(messageConsumer, never()).deleteMessage(anyString(), anyString());
    }
}
