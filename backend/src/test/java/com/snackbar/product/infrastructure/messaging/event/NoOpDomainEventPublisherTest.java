package com.snackbar.product.infrastructure.messaging.event;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.event.ProductCreatedEvent;

import java.math.BigDecimal;

class NoOpDomainEventPublisherTest {

    private NoOpDomainEventPublisher publisher;
    
    @BeforeEach
    void setUp() {
        publisher = new NoOpDomainEventPublisher();
    }
    
    @Test
    void shouldNotThrowExceptionWhenPublishingEvent() {
        // Arrange
        Product product = new Product("1", "Test Product", "Lanche", "Test description", BigDecimal.valueOf(10.99), 5);
        ProductCreatedEvent event = new ProductCreatedEvent(product);
        
        // Act & Assert
        assertDoesNotThrow(() -> publisher.publish(event));
    }
    
    @Test
    void shouldHandleNullEvent() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> publisher.publish(null));
    }
}
