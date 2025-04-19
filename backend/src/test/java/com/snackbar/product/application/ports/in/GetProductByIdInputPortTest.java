package com.snackbar.product.application.ports.in;

import com.snackbar.product.domain.entity.Product;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class GetProductByIdInputPortTest {

    // Test implementation of GetProductByIdInputPort for testing
    private static class TestGetProductByIdInputPort implements GetProductByIdInputPort {
        private String lastRequestedId;
        
        @Override
        public Product getProductById(String id) {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be null or empty");
            }
            
            this.lastRequestedId = id;
            
            // Return a mock product for the requested ID
            return new Product(
                id,
                "Test Product",
                "Lanche",
                "Test Description",
                new BigDecimal("10.0"),
                5
            );
        }
        
        public String getLastRequestedId() {
            return lastRequestedId;
        }
    }

    @Test
    void shouldGetProductById() {
        // Arrange
        TestGetProductByIdInputPort port = new TestGetProductByIdInputPort();
        String productId = "test-product-id";
        
        // Act
        Product product = port.getProductById(productId);
        
        // Assert
        assertEquals(productId, port.getLastRequestedId());
        assertNotNull(product);
        assertEquals(productId, product.id());
        assertEquals("Test Product", product.name());
        assertEquals("Lanche", product.category());
        assertEquals("Test Description", product.description());
        assertEquals(new BigDecimal("10.0"), product.price());
        assertEquals(5, product.cookingTime());
    }
    
    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        // Arrange
        TestGetProductByIdInputPort port = new TestGetProductByIdInputPort();
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> port.getProductById(null)
        );
        
        assertEquals("Product ID cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenIdIsEmpty() {
        // Arrange
        TestGetProductByIdInputPort port = new TestGetProductByIdInputPort();
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> port.getProductById("  ")
        );
        
        assertEquals("Product ID cannot be null or empty", exception.getMessage());
    }
}
