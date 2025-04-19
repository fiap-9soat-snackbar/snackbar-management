package com.snackbar.product.application.usecases;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class UpdateProductByIdUseCaseTest {

    private ProductGateway productGateway;
    private GetProductByIdUseCase getProductByIdUseCase;
    private UpdateProductByIdUseCase updateProductByIdUseCase;

    @BeforeEach
    void setUp() {
        productGateway = mock(ProductGateway.class);
        getProductByIdUseCase = mock(GetProductByIdUseCase.class);
        updateProductByIdUseCase = new UpdateProductByIdUseCase(productGateway, getProductByIdUseCase);
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
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Arrange
        String productId = "1";
        when(getProductByIdUseCase.getProductById(productId)).thenThrow(ProductNotFoundException.withId(productId));

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
            () -> updateProductByIdUseCase.updateProductById(productId, null));
        assertEquals("Product not found with id: " + productId, exception.getMessage());
        verify(getProductByIdUseCase, times(1)).getProductById(productId);
        verify(productGateway, never()).updateProductById(anyString(), any());
    }

    @Test
    void shouldPassUpdatedProductDataToGateway() {
        // Arrange
        String productId = "1";
        Product existingProduct = new Product(productId, "Burger", "Lanche", "Delicious burger", BigDecimal.valueOf(10.99), 15);
        Product updatedProductData = new Product(null, "Burger Deluxe", "Lanche", "Delicious burger with extras", BigDecimal.valueOf(12.99), 20);

        when(getProductByIdUseCase.getProductById(productId)).thenReturn(existingProduct);

        // Act
        updateProductByIdUseCase.updateProductById(productId, updatedProductData);

        // Assert
        verify(productGateway, times(1)).updateProductById(productId, updatedProductData);
    }
}