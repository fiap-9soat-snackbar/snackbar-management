package com.snackbar.product.infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.snackbar.product.infrastructure.messaging.sqs.model.StandardProductMessage;
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
        StandardProductMessage message = mapper.toMessage(event);
        
        // Assert
        assertNotNull(message);
        assertEquals(StandardProductMessage.EVENT_TYPE_CREATED, message.getEventType());
        assertNotNull(message.getTimestamp());
        assertEquals("1", message.getProductId());
        assertEquals("Test Product", message.getName());
        assertEquals("Lanche", message.getCategory());
        assertEquals("Test description", message.getDescription());
        assertEquals(BigDecimal.valueOf(10.99), message.getPrice());
        assertEquals(5, message.getCookingTime());
    }
    
    @Test
    void shouldMapProductUpdatedEventToMessage() {
        // Arrange
        Product product = new Product("1", "Updated Product", "Lanche", "Updated description", BigDecimal.valueOf(12.99), 7);
        ProductUpdatedEvent event = new ProductUpdatedEvent(product);
        
        // Act
        StandardProductMessage message = mapper.toMessage(event);
        
        // Assert
        assertNotNull(message);
        assertEquals(StandardProductMessage.EVENT_TYPE_UPDATED, message.getEventType());
        assertNotNull(message.getTimestamp());
        assertEquals("1", message.getProductId());
        assertEquals("Updated Product", message.getName());
        assertEquals("Lanche", message.getCategory());
        assertEquals("Updated description", message.getDescription());
        assertEquals(BigDecimal.valueOf(12.99), message.getPrice());
        assertEquals(7, message.getCookingTime());
    }
    
    @Test
    void shouldMapProductDeletedEventToMessage() {
        // Arrange
        String productId = "1";
        ProductDeletedEvent event = new ProductDeletedEvent(productId);
        
        // Act
        StandardProductMessage message = mapper.toMessage(event);
        
        // Assert
        assertNotNull(message);
        assertEquals(StandardProductMessage.EVENT_TYPE_DELETED, message.getEventType());
        assertNotNull(message.getTimestamp());
        assertEquals("1", message.getProductId());
        assertNull(message.getName());
        assertNull(message.getCategory());
        assertNull(message.getDescription());
        assertNull(message.getPrice());
        assertNull(message.getCookingTime());
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
        StandardProductMessage message = new StandardProductMessage(StandardProductMessage.EVENT_TYPE_CREATED);
        message.setProductId("1");
        message.setName("Test Product");
        message.setCategory("Lanche");
        message.setDescription("Test description");
        message.setPrice(BigDecimal.valueOf(10.99));
        message.setCookingTime(5);
        
        // Act
        Product product = mapper.toDomainObject(message);
        
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
