package com.snackbar.product.application.ports.in;

import com.snackbar.product.domain.entity.Product;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetProductByCategoryInputPortTest {

    // Test implementation of GetProductByCategoryInputPort for testing
    private static class TestGetProductByCategoryInputPort implements GetProductByCategoryInputPort {
        private String lastRequestedCategory;
        
        @Override
        public List<Product> getProductByCategory(String category) {
            if (category == null || category.trim().isEmpty()) {
                throw new IllegalArgumentException("Category cannot be null or empty");
            }
            
            this.lastRequestedCategory = category;
            
            // Return mock products for the requested category
            List<Product> products = new ArrayList<>();
            products.add(new Product("1", "Product 1", category, "Description 1", new BigDecimal("10.0"), 5));
            products.add(new Product("2", "Product 2", category, "Description 2", new BigDecimal("20.0"), 10));
            
            return products;
        }
        
        public String getLastRequestedCategory() {
            return lastRequestedCategory;
        }
    }

    @Test
    void shouldGetProductsByCategory() {
        // Arrange
        TestGetProductByCategoryInputPort port = new TestGetProductByCategoryInputPort();
        String category = "Lanche";
        
        // Act
        List<Product> products = port.getProductByCategory(category);
        
        // Assert
        assertEquals(category, port.getLastRequestedCategory());
        assertEquals(2, products.size());
        assertEquals(category, products.get(0).category());
        assertEquals(category, products.get(1).category());
    }
    
    @Test
    void shouldThrowExceptionWhenCategoryIsNull() {
        // Arrange
        TestGetProductByCategoryInputPort port = new TestGetProductByCategoryInputPort();
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> port.getProductByCategory(null)
        );
        
        assertEquals("Category cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenCategoryIsEmpty() {
        // Arrange
        TestGetProductByCategoryInputPort port = new TestGetProductByCategoryInputPort();
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> port.getProductByCategory("  ")
        );
        
        assertEquals("Category cannot be null or empty", exception.getMessage());
    }
}
