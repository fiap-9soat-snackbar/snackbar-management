package com.snackbar.product.application.usecases;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.application.ports.out.DomainEventPublisher;
import com.snackbar.product.domain.event.ProductDeletedEvent;
import com.snackbar.product.domain.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeleteProductByIdUseCaseTest {

    private ProductGateway productGateway;
    private DomainEventPublisher eventPublisher;
    private DeleteProductByIdUseCase deleteProductByIdUseCase;

    @BeforeEach
    void setUp() {
        productGateway = mock(ProductGateway.class);
        eventPublisher = mock(DomainEventPublisher.class);
        deleteProductByIdUseCase = new DeleteProductByIdUseCase(productGateway, eventPublisher);
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        // Arrange
        String productId = "1";
        doNothing().when(productGateway).deleteProductById(productId);

        // Act
        deleteProductByIdUseCase.deleteProductById(productId);

        // Assert
        verify(productGateway, times(1)).deleteProductById(productId);
        verify(eventPublisher, times(1)).publish(any(ProductDeletedEvent.class));
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
        verify(eventPublisher, never()).publish(any());
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
        verify(eventPublisher, never()).publish(any());
        
        // Verify no further actions are taken after exception
        verifyNoMoreInteractions(productGateway);
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> deleteProductByIdUseCase.deleteProductById(null));
        assertEquals("Product ID cannot be null or empty", exception.getMessage());
        verify(productGateway, never()).deleteProductById(anyString());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowExceptionWhenIdIsEmpty() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> deleteProductByIdUseCase.deleteProductById("  "));
        assertEquals("Product ID cannot be null or empty", exception.getMessage());
        verify(productGateway, never()).deleteProductById(anyString());
        verify(eventPublisher, never()).publish(any());
    }
}
