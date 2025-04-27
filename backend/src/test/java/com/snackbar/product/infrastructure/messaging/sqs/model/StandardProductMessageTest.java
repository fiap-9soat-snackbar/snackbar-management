package com.snackbar.product.infrastructure.messaging.sqs.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

class StandardProductMessageTest {

    @Test
    void shouldCreateStandardProductMessageWithDefaultConstructor() {
        // Act
        StandardProductMessage message = new StandardProductMessage();
        
        // Assert
        assertNotNull(message.getMessageId());
        assertNull(message.getEventType());
        assertNotNull(message.getTimestamp());
        assertNull(message.getProductId());
        assertNull(message.getName());
        assertNull(message.getCategory());
        assertNull(message.getDescription());
        assertNull(message.getPrice());
        assertNull(message.getCookingTime());
    }
    
    @Test
    void shouldCreateStandardProductMessageWithEventType() {
        // Act
        StandardProductMessage message = new StandardProductMessage(StandardProductMessage.EVENT_TYPE_CREATED);
        
        // Assert
        assertNotNull(message.getMessageId());
        assertEquals(StandardProductMessage.EVENT_TYPE_CREATED, message.getEventType());
        assertNotNull(message.getTimestamp());
        assertNull(message.getProductId());
        assertNull(message.getName());
        assertNull(message.getCategory());
        assertNull(message.getDescription());
        assertNull(message.getPrice());
        assertNull(message.getCookingTime());
    }
    
    @Test
    void shouldCreateStandardProductMessageWithAllFields() {
        // Arrange
        String messageId = "test-message-id";
        String eventType = StandardProductMessage.EVENT_TYPE_CREATED;
        Instant timestamp = Instant.now();
        String productId = "1";
        String name = "Test Product";
        String category = "Lanche";
        String description = "Test description";
        BigDecimal price = BigDecimal.valueOf(10.99);
        Integer cookingTime = 5;
        
        // Act
        StandardProductMessage message = new StandardProductMessage(
                messageId, eventType, timestamp, 
                productId, name, category, description, 
                price, cookingTime);
        
        // Assert
        assertEquals(messageId, message.getMessageId());
        assertEquals(eventType, message.getEventType());
        assertEquals(timestamp, message.getTimestamp());
        assertEquals(productId, message.getProductId());
        assertEquals(name, message.getName());
        assertEquals(category, message.getCategory());
        assertEquals(description, message.getDescription());
        assertEquals(price, message.getPrice());
        assertEquals(cookingTime, message.getCookingTime());
    }
    
    @Test
    void shouldSetAndGetAllFields() {
        // Arrange
        StandardProductMessage message = new StandardProductMessage();
        String messageId = "test-message-id";
        String eventType = StandardProductMessage.EVENT_TYPE_UPDATED;
        Instant timestamp = Instant.now();
        String productId = "1";
        String name = "Test Product";
        String category = "Lanche";
        String description = "Test description";
        BigDecimal price = BigDecimal.valueOf(10.99);
        Integer cookingTime = 5;
        
        // Act
        message.setMessageId(messageId);
        message.setEventType(eventType);
        message.setTimestamp(timestamp);
        message.setProductId(productId);
        message.setName(name);
        message.setCategory(category);
        message.setDescription(description);
        message.setPrice(price);
        message.setCookingTime(cookingTime);
        
        // Assert
        assertEquals(messageId, message.getMessageId());
        assertEquals(eventType, message.getEventType());
        assertEquals(timestamp, message.getTimestamp());
        assertEquals(productId, message.getProductId());
        assertEquals(name, message.getName());
        assertEquals(category, message.getCategory());
        assertEquals(description, message.getDescription());
        assertEquals(price, message.getPrice());
        assertEquals(cookingTime, message.getCookingTime());
    }
    
    @Test
    void shouldVerifyEventTypeConstants() {
        // Assert
        assertEquals("PRODUCT_CREATED", StandardProductMessage.EVENT_TYPE_CREATED);
        assertEquals("PRODUCT_UPDATED", StandardProductMessage.EVENT_TYPE_UPDATED);
        assertEquals("PRODUCT_DELETED", StandardProductMessage.EVENT_TYPE_DELETED);
    }
    
    @Test
    void shouldHaveJsonIgnorePropertiesAnnotation() {
        // Verify that the class has the JsonIgnoreProperties annotation
        assertTrue(StandardProductMessage.class.isAnnotationPresent(com.fasterxml.jackson.annotation.JsonIgnoreProperties.class));
        
        // Verify that the annotation has the ignoreUnknown attribute set to true
        com.fasterxml.jackson.annotation.JsonIgnoreProperties annotation = 
                StandardProductMessage.class.getAnnotation(com.fasterxml.jackson.annotation.JsonIgnoreProperties.class);
        assertTrue(annotation.ignoreUnknown());
    }
}
