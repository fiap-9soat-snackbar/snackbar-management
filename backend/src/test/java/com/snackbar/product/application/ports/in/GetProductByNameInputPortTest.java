package com.snackbar.product.application.ports.in;

import com.snackbar.product.domain.entity.Product;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class GetProductByNameInputPortTest {

    // Test implementation of GetProductByNameInputPort for testing
    private static class TestGetProductByNameInputPort implements GetProductByNameInputPort {
        private String lastRequestedName;
        
        @Override
        public Product getProductByName(String name) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be null or empty");
            }
            
            this.lastRequestedName = name;
            
            // Return a mock product for the requested name
            return new Product(
                "test-id",
                name,
                "Lanche",
                "Test Description",
                new BigDecimal("10.0"),
                5
            );
        }
        
        public String getLastRequestedName() {
            return lastRequestedName;
        }
    }

    @Test
    void shouldGetProductByName() {
        // Arrange
        TestGetProductByNameInputPort port = new TestGetProductByNameInputPort();
        String productName = "Test Product";
        
        // Act
        Product product = port.getProductByName(productName);
        
        // Assert
        assertEquals(productName, port.getLastRequestedName());
        assertNotNull(product);
        assertEquals("test-id", product.id());
        assertEquals(productName, product.name());
        assertEquals("Lanche", product.category());
        assertEquals("Test Description", product.description());
        assertEquals(new BigDecimal("10.0"), product.price());
        assertEquals(5, product.cookingTime());
    }
    
    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        // Arrange
        TestGetProductByNameInputPort port = new TestGetProductByNameInputPort();
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> port.getProductByName(null)
        );
        
        assertEquals("Product name cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        // Arrange
        TestGetProductByNameInputPort port = new TestGetProductByNameInputPort();
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> port.getProductByName("  ")
        );
        
        assertEquals("Product name cannot be null or empty", exception.getMessage());
    }
}
