package com.snackbar.product.application.usecases;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.domain.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeleteProductByIdUseCaseTest {

    private ProductGateway productGateway;
    private DeleteProductByIdUseCase deleteProductByIdUseCase;

    @BeforeEach
    void setUp() {
        productGateway = mock(ProductGateway.class);
        deleteProductByIdUseCase = new DeleteProductByIdUseCase(productGateway);
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        // Arrange
        String productId = "1";
        doNothing().when(productGateway).deleteProductById(productId);

        // Act & Assert
        assertDoesNotThrow(() -> deleteProductByIdUseCase.deleteProductById(productId));
        verify(productGateway, times(1)).deleteProductById(productId);
    }

    @Test
    void shouldPropagateExceptionWhenProductNotFound() {
        // Arrange
        String nonExistentId = "999";
        doThrow(ProductNotFoundException.withId(nonExistentId)).when(productGateway).deleteProductById(nonExistentId);

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, 
            () -> deleteProductByIdUseCase.deleteProductById(nonExistentId));
        assertEquals("Product not found with id: " + nonExistentId, exception.getMessage());
        verify(productGateway, times(1)).deleteProductById(nonExistentId);
    }
    
    @Test
    void shouldThrowExceptionWhenGatewayFails() {
        // Arrange
        String productId = "1";
        doThrow(new RuntimeException("Database error")).when(productGateway).deleteProductById(productId);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> deleteProductByIdUseCase.deleteProductById(productId));
        assertEquals("Database error", exception.getMessage());
        verify(productGateway, times(1)).deleteProductById(productId);
        
        // Verify no further actions are taken after exception
        verifyNoMoreInteractions(productGateway);
    }
}
