package com.snackbar.product.application.ports.in;

import com.snackbar.product.domain.entity.Product;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UpdateProductByIdInputPortTest {

    // Test implementation of UpdateProductByIdInputPort for testing
    private static class TestUpdateProductByIdInputPort implements UpdateProductByIdInputPort {
        private String lastRequestedId;
        private Product lastUpdatedProduct;
        
        @Override
        public Product updateProductById(String id, Product product) {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be null or empty");
            }
            
            if (product == null) {
                throw new IllegalArgumentException("Product cannot be null");
            }
            
            this.lastRequestedId = id;
            this.lastUpdatedProduct = product;
            
            // Return an updated product with the specified ID
            return new Product(
                id,
                product.name(),
                product.category(),
                product.description(),
                product.price(),
                product.cookingTime()
            );
        }
        
        public String getLastRequestedId() {
            return lastRequestedId;
        }
        
        public Product getLastUpdatedProduct() {
            return lastUpdatedProduct;
        }
    }

    @Test
    void shouldUpdateProductById() {
        // Arrange
        TestUpdateProductByIdInputPort port = new TestUpdateProductByIdInputPort();
        String productId = "test-product-id";
        Product product = new Product(
            null,
            "Updated Product",
            "Bebida",
            "Updated Description",
            new BigDecimal("15.0"),
            7
        );
        
        // Act
        Product updatedProduct = port.updateProductById(productId, product);
        
        // Assert
        assertEquals(productId, port.getLastRequestedId());
        assertSame(product, port.getLastUpdatedProduct());
        
        assertNotNull(updatedProduct);
        assertEquals(productId, updatedProduct.id());
        assertEquals("Updated Product", updatedProduct.name());
        assertEquals("Bebida", updatedProduct.category());
        assertEquals("Updated Description", updatedProduct.description());
        assertEquals(new BigDecimal("15.0"), updatedProduct.price());
        assertEquals(7, updatedProduct.cookingTime());
    }
    
    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        // Arrange
        TestUpdateProductByIdInputPort port = new TestUpdateProductByIdInputPort();
        Product product = new Product(
            null,
            "Updated Product",
            "Bebida",
            "Updated Description",
            new BigDecimal("15.0"),
            7
        );
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> port.updateProductById(null, product)
        );
        
        assertEquals("Product ID cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenIdIsEmpty() {
        // Arrange
        TestUpdateProductByIdInputPort port = new TestUpdateProductByIdInputPort();
        Product product = new Product(
            null,
            "Updated Product",
            "Bebida",
            "Updated Description",
            new BigDecimal("15.0"),
            7
        );
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> port.updateProductById("  ", product)
        );
        
        assertEquals("Product ID cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenProductIsNull() {
        // Arrange
        TestUpdateProductByIdInputPort port = new TestUpdateProductByIdInputPort();
        String productId = "test-product-id";
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> port.updateProductById(productId, null)
        );
        
        assertEquals("Product cannot be null", exception.getMessage());
    }
}
