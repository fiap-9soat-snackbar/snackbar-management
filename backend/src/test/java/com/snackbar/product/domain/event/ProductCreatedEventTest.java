package com.snackbar.product.domain.event;

import com.snackbar.product.domain.entity.Product;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductCreatedEventTest {

    @Test
    void shouldStoreProductReference() {
        // Arrange
        Product product = new Product(
            "1", 
            "Test Product", 
            "Lanche", 
            "Test Description", 
            new BigDecimal("10.0"), 
            5
        );
        
        // Act
        ProductCreatedEvent event = new ProductCreatedEvent(product);
        
        // Assert
        assertSame(product, event.getProduct());
    }

    @Test
    void shouldInheritFromDomainEvent() {
        // Arrange
        Product product = new Product(
            "1", 
            "Test Product", 
            "Lanche", 
            "Test Description", 
            new BigDecimal("10.0"), 
            5
        );
        
        // Act
        ProductCreatedEvent event = new ProductCreatedEvent(product);
        
        // Assert
        assertTrue(event instanceof DomainEvent);
        assertNotNull(event.getEventId());
        assertNotNull(event.getOccurredOn());
    }
}
