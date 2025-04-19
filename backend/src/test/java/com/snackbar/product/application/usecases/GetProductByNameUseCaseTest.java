package com.snackbar.product.application.usecases;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class GetProductByNameUseCaseTest {

    private ProductGateway productGateway;
    private GetProductByNameUseCase getProductByNameUseCase;

    @BeforeEach
    void setUp() {
        productGateway = mock(ProductGateway.class);
        getProductByNameUseCase = new GetProductByNameUseCase(productGateway);
    }

    @Test
    void shouldReturnProductWhenNameExists() {
        // Arrange
        String productName = "Burger";
        Product expectedProduct = new Product("1", productName, "Lanche", "Delicious burger", BigDecimal.valueOf(10.99), 15);
        
        when(productGateway.getProductByName(productName)).thenReturn(expectedProduct);

        // Act
        Product result = getProductByNameUseCase.getProductByName(productName);

        // Assert
        assertNotNull(result);
        assertEquals("1", result.id());
        assertEquals(productName, result.name());
        assertEquals("Lanche", result.category());
        assertEquals("Delicious burger", result.description());
        assertEquals(BigDecimal.valueOf(10.99), result.price());
        assertEquals(15, result.cookingTime());
        verify(productGateway, times(1)).getProductByName(productName);
    }

    @Test
    void shouldPropagateExceptionWhenProductNameNotFound() {
        // Arrange
        String nonExistentName = "NonExistentBurger";
        when(productGateway.getProductByName(nonExistentName)).thenThrow(ProductNotFoundException.withName(nonExistentName));

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, 
            () -> getProductByNameUseCase.getProductByName(nonExistentName));
        assertEquals("Product not found with name: " + nonExistentName, exception.getMessage());
        verify(productGateway, times(1)).getProductByName(nonExistentName);
        
        // Verify no further actions are taken after exception
        verifyNoMoreInteractions(productGateway);
    }
    
    @Test
    void shouldThrowExceptionWhenGatewayFails() {
        // Arrange
        String productName = "Burger";
        when(productGateway.getProductByName(productName)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> getProductByNameUseCase.getProductByName(productName));
        assertEquals("Database error", exception.getMessage());
        verify(productGateway, times(1)).getProductByName(productName);
    }
}
