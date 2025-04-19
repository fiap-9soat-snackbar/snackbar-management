package com.snackbar.product.domain.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductDeletedEventTest {

    @Test
    void shouldStoreProductId() {
        // Arrange
        String productId = "test-product-id";
        
        // Act
        ProductDeletedEvent event = new ProductDeletedEvent(productId);
        
        // Assert
        assertEquals(productId, event.getProductId());
    }

    @Test
    void shouldInheritFromDomainEvent() {
        // Arrange
        String productId = "test-product-id";
        
        // Act
        ProductDeletedEvent event = new ProductDeletedEvent(productId);
        
        // Assert
        assertTrue(event instanceof DomainEvent);
        assertNotNull(event.getEventId());
        assertNotNull(event.getOccurredOn());
    }
    
    @Test
    void shouldHandleNullProductId() {
        // This test verifies that the event can be created with a null ID
        // (though in practice we should validate this at a higher level)
        
        // Act & Assert
        assertDoesNotThrow(() -> new ProductDeletedEvent(null));
    }
}
