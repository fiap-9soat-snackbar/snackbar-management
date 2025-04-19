package com.snackbar.product.infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

class ProductMessageTest {

    @Test
    void shouldCreateProductMessageWithAllFields() {
        // Arrange
        String messageId = "test-message-id";
        String eventType = "PRODUCT_CREATED";
        Instant timestamp = Instant.now();
        ProductMessage.ProductData productData = new ProductMessage.ProductData(
            "1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5
        );
        
        // Act
        ProductMessage message = new ProductMessage(messageId, eventType, timestamp, productData);
        
        // Assert
        assertEquals(messageId, message.getMessageId());
        assertEquals(eventType, message.getEventType());
        assertEquals(timestamp, message.getTimestamp());
        assertNotNull(message.getProductData());
        assertEquals("1", message.getProductData().getId());
        assertEquals("Test Product", message.getProductData().getName());
        assertEquals("Lanche", message.getProductData().getCategory());
        assertEquals("Test description", message.getProductData().getDescription());
        assertEquals(BigDecimal.valueOf(10.99), message.getProductData().getPrice());
        assertEquals(5, message.getProductData().getCookingTime());
    }
    
    @Test
    void shouldCreateEmptyProductMessageAndSetFields() {
        // Arrange
        ProductMessage message = new ProductMessage();
        String messageId = "test-message-id";
        String eventType = "PRODUCT_UPDATED";
        Instant timestamp = Instant.now();
        ProductMessage.ProductData productData = new ProductMessage.ProductData();
        
        // Act
        message.setMessageId(messageId);
        message.setEventType(eventType);
        message.setTimestamp(timestamp);
        message.setProductData(productData);
        
        // Assert
        assertEquals(messageId, message.getMessageId());
        assertEquals(eventType, message.getEventType());
        assertEquals(timestamp, message.getTimestamp());
        assertSame(productData, message.getProductData());
    }
    
    @Test
    void shouldCreateEmptyProductDataAndSetFields() {
        // Arrange
        ProductMessage.ProductData productData = new ProductMessage.ProductData();
        
        // Act
        productData.setId("1");
        productData.setName("Test Product");
        productData.setCategory("Lanche");
        productData.setDescription("Test description");
        productData.setPrice(BigDecimal.valueOf(10.99));
        productData.setCookingTime(5);
        
        // Assert
        assertEquals("1", productData.getId());
        assertEquals("Test Product", productData.getName());
        assertEquals("Lanche", productData.getCategory());
        assertEquals("Test description", productData.getDescription());
        assertEquals(BigDecimal.valueOf(10.99), productData.getPrice());
        assertEquals(5, productData.getCookingTime());
    }
}
