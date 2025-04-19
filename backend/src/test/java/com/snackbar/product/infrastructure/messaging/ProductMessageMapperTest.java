package com.snackbar.product.infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.event.ProductCreatedEvent;
import com.snackbar.product.domain.event.ProductDeletedEvent;
import com.snackbar.product.domain.event.ProductUpdatedEvent;

class ProductMessageMapperTest {

    private ProductMessageMapper mapper;
    
    @BeforeEach
    void setUp() {
        mapper = new ProductMessageMapper();
    }
    
    @Test
    void shouldMapProductCreatedEventToMessage() {
        // Arrange
        Product product = new Product("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5);
        ProductCreatedEvent event = new ProductCreatedEvent(product);
        
        // Act
        ProductMessage message = mapper.toMessage(event);
        
        // Assert
        assertNotNull(message);
        assertNotNull(message.getMessageId());
        assertEquals("PRODUCT_CREATED", message.getEventType());
        
        // Convert LocalDateTime to Instant for comparison
        Instant expectedInstant = event.getOccurredOn().atZone(ZoneOffset.UTC).toInstant();
        assertEquals(expectedInstant, message.getTimestamp());
        
        ProductMessage.ProductData productData = message.getProductData();
        assertNotNull(productData);
        assertEquals("1", productData.getId());
        assertEquals("Test Product", productData.getName());
        assertEquals("Lanche", productData.getCategory());
        assertEquals("Test description", productData.getDescription());
        assertEquals(BigDecimal.valueOf(10.99), productData.getPrice());
        assertEquals(5, productData.getCookingTime());
    }
    
    @Test
    void shouldMapProductUpdatedEventToMessage() {
        // Arrange
        Product product = new Product("1", "Updated Product", "Lanche", "Updated description", BigDecimal.valueOf(12.99), 7);
        ProductUpdatedEvent event = new ProductUpdatedEvent(product);
        
        // Act
        ProductMessage message = mapper.toMessage(event);
        
        // Assert
        assertNotNull(message);
        assertNotNull(message.getMessageId());
        assertEquals("PRODUCT_UPDATED", message.getEventType());
        
        // Convert LocalDateTime to Instant for comparison
        Instant expectedInstant = event.getOccurredOn().atZone(ZoneOffset.UTC).toInstant();
        assertEquals(expectedInstant, message.getTimestamp());
        
        ProductMessage.ProductData productData = message.getProductData();
        assertNotNull(productData);
        assertEquals("1", productData.getId());
        assertEquals("Updated Product", productData.getName());
        assertEquals("Lanche", productData.getCategory());
        assertEquals("Updated description", productData.getDescription());
        assertEquals(BigDecimal.valueOf(12.99), productData.getPrice());
        assertEquals(7, productData.getCookingTime());
    }
    
    @Test
    void shouldMapProductDeletedEventToMessage() {
        // Arrange
        String productId = "1";
        ProductDeletedEvent event = new ProductDeletedEvent(productId);
        
        // Act
        ProductMessage message = mapper.toMessage(event);
        
        // Assert
        assertNotNull(message);
        assertNotNull(message.getMessageId());
        assertEquals("PRODUCT_DELETED", message.getEventType());
        
        // Convert LocalDateTime to Instant for comparison
        Instant expectedInstant = event.getOccurredOn().atZone(ZoneOffset.UTC).toInstant();
        assertEquals(expectedInstant, message.getTimestamp());
        
        ProductMessage.ProductData productData = message.getProductData();
        assertNotNull(productData);
        assertEquals("1", productData.getId());
        assertNull(productData.getName());
        assertNull(productData.getCategory());
        assertNull(productData.getDescription());
        assertNull(productData.getPrice());
        assertEquals(0, productData.getCookingTime());
    }
    
    @Test
    void shouldThrowExceptionForUnsupportedEventType() {
        // Arrange
        class UnsupportedEvent extends com.snackbar.product.domain.event.DomainEvent {
            // Custom unsupported event type
        }
        
        UnsupportedEvent event = new UnsupportedEvent();
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            mapper.toMessage(event);
        });
        
        assertTrue(exception.getMessage().contains("Unsupported event type"));
    }
    
    @Test
    void shouldMapProductMessageToProduct() {
        // Arrange
        ProductMessage.ProductData productData = new ProductMessage.ProductData(
            "1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5
        );
        ProductMessage message = new ProductMessage("msg-id", "PRODUCT_CREATED", Instant.now(), productData);
        
        // Act
        Product product = mapper.toProduct(message);
        
        // Assert
        assertNotNull(product);
        assertEquals("1", product.id());
        assertEquals("Test Product", product.name());
        assertEquals("Lanche", product.category());
        assertEquals("Test description", product.description());
        assertEquals(BigDecimal.valueOf(10.99), product.price());
        assertEquals(5, product.cookingTime());
    }
}
