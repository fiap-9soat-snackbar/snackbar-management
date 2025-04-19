package com.snackbar.product.domain.event;

import com.snackbar.product.domain.entity.Product;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductUpdatedEventTest {

    @Test
    void shouldStoreProductReference() {
        // Arrange
        Product product = new Product(
            "1", 
            "Updated Product", 
            "Bebida", 
            "Updated Description", 
            new BigDecimal("15.0"), 
            3
        );
        
        // Act
        ProductUpdatedEvent event = new ProductUpdatedEvent(product);
        
        // Assert
        assertSame(product, event.getProduct());
    }

    @Test
    void shouldInheritFromDomainEvent() {
        // Arrange
        Product product = new Product(
            "1", 
            "Updated Product", 
            "Bebida", 
            "Updated Description", 
            new BigDecimal("15.0"), 
            3
        );
        
        // Act
        ProductUpdatedEvent event = new ProductUpdatedEvent(product);
        
        // Assert
        assertTrue(event instanceof DomainEvent);
        assertNotNull(event.getEventId());
        assertNotNull(event.getOccurredOn());
    }
}
