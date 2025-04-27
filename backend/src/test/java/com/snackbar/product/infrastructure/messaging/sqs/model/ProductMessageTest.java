package com.snackbar.product.infrastructure.messaging.sqs.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

class ProductMessageTest {

    @Test
    void shouldCreateProductMessageWithDefaultConstructor() {
        // Act
        ProductMessage message = new ProductMessage();
        
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
    void shouldCreateProductMessageWithEventType() {
        // Act
        ProductMessage message = new ProductMessage(ProductMessage.EVENT_TYPE_CREATED);
        
        // Assert
        assertNotNull(message.getMessageId());
        assertEquals(ProductMessage.EVENT_TYPE_CREATED, message.getEventType());
        assertNotNull(message.getTimestamp());
        assertNull(message.getProductId());
        assertNull(message.getName());
        assertNull(message.getCategory());
        assertNull(message.getDescription());
        assertNull(message.getPrice());
        assertNull(message.getCookingTime());
    }
    
    @Test
    void shouldCreateProductMessageWithAllFields() {
        // Arrange
        String messageId = "test-message-id";
        String eventType = ProductMessage.EVENT_TYPE_CREATED;
        Instant timestamp = Instant.now();
        String productId = "1";
        String name = "Test Product";
        String category = "Lanche";
        String description = "Test description";
        BigDecimal price = BigDecimal.valueOf(10.99);
        Integer cookingTime = 5;
        
        // Act
        ProductMessage message = new ProductMessage(
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
        ProductMessage message = new ProductMessage();
        String messageId = "test-message-id";
        String eventType = ProductMessage.EVENT_TYPE_UPDATED;
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
        assertEquals("PRODUCT_CREATED", ProductMessage.EVENT_TYPE_CREATED);
        assertEquals("PRODUCT_UPDATED", ProductMessage.EVENT_TYPE_UPDATED);
        assertEquals("PRODUCT_DELETED", ProductMessage.EVENT_TYPE_DELETED);
    }
}
