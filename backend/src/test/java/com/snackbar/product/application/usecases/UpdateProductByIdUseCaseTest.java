package com.snackbar.product.application.usecases;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.application.ports.out.DomainEventPublisher;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.event.ProductUpdatedEvent;
import com.snackbar.product.domain.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class UpdateProductByIdUseCaseTest {

    private ProductGateway productGateway;
    private GetProductByIdUseCase getProductByIdUseCase;
    private DomainEventPublisher eventPublisher;
    private UpdateProductByIdUseCase updateProductByIdUseCase;

    @BeforeEach
    void setUp() {
        productGateway = mock(ProductGateway.class);
        getProductByIdUseCase = mock(GetProductByIdUseCase.class);
        eventPublisher = mock(DomainEventPublisher.class);
        updateProductByIdUseCase = new UpdateProductByIdUseCase(productGateway, getProductByIdUseCase, eventPublisher);
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        // Arrange
        String productId = "1";
        Product existingProduct = new Product(productId, "Burger", "Lanche", "Delicious burger", BigDecimal.valueOf(10.99), 15);
        Product updatedProductData = new Product(null, "Burger Deluxe", "Lanche", "Delicious burger with extras", BigDecimal.valueOf(12.99), 20);
        Product updatedProduct = new Product(productId, "Burger Deluxe", "Lanche", "Delicious burger with extras", BigDecimal.valueOf(12.99), 20);

        when(getProductByIdUseCase.getProductById(productId)).thenReturn(existingProduct);
        when(productGateway.updateProductById(productId, updatedProductData)).thenReturn(updatedProduct);

        // Act
        Product result = updateProductByIdUseCase.updateProductById(productId, updatedProductData);

        // Assert
        assertNotNull(result);
        assertEquals("Burger Deluxe", result.name());
        assertEquals(BigDecimal.valueOf(12.99), result.price());
        verify(getProductByIdUseCase, times(1)).getProductById(productId);
        verify(productGateway, times(1)).updateProductById(productId, updatedProductData);
        verify(eventPublisher, times(1)).publish(any(ProductUpdatedEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Arrange
        String productId = "1";
        Product product = new Product(null, "Test", "Lanche", "Test description for product", BigDecimal.valueOf(10.0), 5);
        when(getProductByIdUseCase.getProductById(productId)).thenThrow(ProductNotFoundException.withId(productId));

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
            () -> updateProductByIdUseCase.updateProductById(productId, product));
        assertEquals("Product not found with id: " + productId, exception.getMessage());
        verify(getProductByIdUseCase, times(1)).getProductById(productId);
        verify(productGateway, never()).updateProductById(anyString(), any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        // Arrange
        Product product = new Product(null, "Test", "Lanche", "Test description for product", BigDecimal.valueOf(10.0), 5);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> updateProductByIdUseCase.updateProductById(null, product));
        assertEquals("Product ID cannot be null or empty", exception.getMessage());
        verify(getProductByIdUseCase, never()).getProductById(anyString());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowExceptionWhenProductIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> updateProductByIdUseCase.updateProductById("1", null));
        assertEquals("Product cannot be null", exception.getMessage());
        verify(getProductByIdUseCase, never()).getProductById(anyString());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void shouldPassUpdatedProductDataToGateway() {
        // Arrange
        String productId = "1";
        Product existingProduct = new Product(productId, "Burger", "Lanche", "Delicious burger", BigDecimal.valueOf(10.99), 15);
        Product updatedProductData = new Product(null, "Burger Deluxe", "Lanche", "Delicious burger with extras", BigDecimal.valueOf(12.99), 20);
        Product updatedProduct = new Product(productId, "Burger Deluxe", "Lanche", "Delicious burger with extras", BigDecimal.valueOf(12.99), 20);

        when(getProductByIdUseCase.getProductById(productId)).thenReturn(existingProduct);
        when(productGateway.updateProductById(productId, updatedProductData)).thenReturn(updatedProduct);

        // Act
        updateProductByIdUseCase.updateProductById(productId, updatedProductData);

        // Assert
        verify(productGateway, times(1)).updateProductById(productId, updatedProductData);
    }
}