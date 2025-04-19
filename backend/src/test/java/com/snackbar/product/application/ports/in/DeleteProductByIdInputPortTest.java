package com.snackbar.product.application.ports.in;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeleteProductByIdInputPortTest {

    // Test implementation of DeleteProductByIdInputPort for testing
    private static class TestDeleteProductByIdInputPort implements DeleteProductByIdInputPort {
        private final List<String> deletedIds = new ArrayList<>();
        
        @Override
        public void deleteProductById(String id) {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be null or empty");
            }
            
            deletedIds.add(id);
        }
        
        public List<String> getDeletedIds() {
            return deletedIds;
        }
    }

    @Test
    void shouldDeleteProductById() {
        // Arrange
        TestDeleteProductByIdInputPort port = new TestDeleteProductByIdInputPort();
        String productId = "test-product-id";
        
        // Act
        port.deleteProductById(productId);
        
        // Assert
        assertEquals(1, port.getDeletedIds().size());
        assertEquals(productId, port.getDeletedIds().get(0));
    }
    
    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        // Arrange
        TestDeleteProductByIdInputPort port = new TestDeleteProductByIdInputPort();
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> port.deleteProductById(null)
        );
        
        assertEquals("Product ID cannot be null or empty", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenIdIsEmpty() {
        // Arrange
        TestDeleteProductByIdInputPort port = new TestDeleteProductByIdInputPort();
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> port.deleteProductById("  ")
        );
        
        assertEquals("Product ID cannot be null or empty", exception.getMessage());
    }
}
