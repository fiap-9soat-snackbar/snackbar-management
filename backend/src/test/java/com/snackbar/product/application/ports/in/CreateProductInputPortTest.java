package com.snackbar.product.application.ports.in;

import com.snackbar.product.domain.entity.Product;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CreateProductInputPortTest {

    // Test implementation of CreateProductInputPort for testing
    private static class TestCreateProductInputPort implements CreateProductInputPort {
        private Product lastCreatedProduct;
        
        @Override
        public Product createProduct(Product product) {
            if (product == null) {
                throw new IllegalArgumentException("Product cannot be null");
            }
            
            // Simulate ID generation
            Product createdProduct = new Product(
                "generated-id",
                product.name(),
                product.category(),
                product.description(),
                product.price(),
                product.cookingTime()
            );
            
            this.lastCreatedProduct = createdProduct;
            return createdProduct;
        }
        
        public Product getLastCreatedProduct() {
            return lastCreatedProduct;
        }
    }

    @Test
    void shouldCreateProduct() {
        // Arrange
        TestCreateProductInputPort port = new TestCreateProductInputPort();
        Product product = new Product(
            null,
            "Test Product",
            "Lanche",
            "Test Description",
            new BigDecimal("10.0"),
            5
        );
        
        // Act
        Product createdProduct = port.createProduct(product);
        
        // Assert
        assertNotNull(createdProduct);
        assertEquals("generated-id", createdProduct.id());
        assertEquals("Test Product", createdProduct.name());
        assertEquals("Lanche", createdProduct.category());
        assertEquals("Test Description", createdProduct.description());
        assertEquals(new BigDecimal("10.0"), createdProduct.price());
        assertEquals(5, createdProduct.cookingTime());
        
        assertSame(createdProduct, port.getLastCreatedProduct());
    }
    
    @Test
    void shouldThrowExceptionWhenProductIsNull() {
        // Arrange
        TestCreateProductInputPort port = new TestCreateProductInputPort();
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> port.createProduct(null)
        );
        
        assertEquals("Product cannot be null", exception.getMessage());
    }
}
