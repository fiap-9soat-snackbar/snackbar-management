package com.snackbar.product.application.usecases;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

class GetProductByIdUseCaseTest {

    private ProductGateway productGateway;
    private GetProductByIdUseCase getProductByIdUseCase;

    @BeforeEach
    void setUp() {
        productGateway = mock(ProductGateway.class);
        getProductByIdUseCase = new GetProductByIdUseCase(productGateway);
    }

    @Nested
    @DisplayName("When product exists")
    class WhenProductExists {
        @Test
        @DisplayName("Should return product when ID exists")
        void shouldReturnProductWhenIdExists() {
            // Arrange
            String productId = "1";
            Product expectedProduct = new Product(productId, "Burger", "Lanche", "Delicious burger", BigDecimal.valueOf(10.99), 15);
            
            when(productGateway.getProductById(productId)).thenReturn(expectedProduct);

            // Act
            Product result = getProductByIdUseCase.getProductById(productId);

            // Assert
            assertNotNull(result);
            assertEquals(productId, result.id());
            assertEquals("Burger", result.name());
            assertEquals("Lanche", result.category());
            assertEquals("Delicious burger", result.description());
            assertEquals(BigDecimal.valueOf(10.99), result.price());
            assertEquals(15, result.cookingTime());
            verify(productGateway, times(1)).getProductById(productId);
        }
    }

    @Nested
    @DisplayName("When product does not exist")
    class WhenProductDoesNotExist {
        @Test
        @DisplayName("Should throw exception when product not found")
        void shouldThrowExceptionWhenProductNotFound() {
            // Arrange
            String nonExistentId = "999";
            when(productGateway.getProductById(nonExistentId)).thenThrow(ProductNotFoundException.withId(nonExistentId));

            // Act & Assert
            ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, 
                () -> getProductByIdUseCase.getProductById(nonExistentId));
            assertEquals("Product not found with id: " + nonExistentId, exception.getMessage());
            verify(productGateway, times(1)).getProductById(nonExistentId);
            
            // Verify no further actions are taken after exception
            verifyNoMoreInteractions(productGateway);
        }
    }
    
    @Nested
    @DisplayName("When gateway fails")
    class WhenGatewayFails {
        @Test
        @DisplayName("Should throw exception when gateway fails")
        void shouldThrowExceptionWhenGatewayFails() {
            // Arrange
            String productId = "1";
            when(productGateway.getProductById(productId)).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> getProductByIdUseCase.getProductById(productId));
            assertEquals("Database error", exception.getMessage());
            verify(productGateway, times(1)).getProductById(productId);
        }
    }
    
    @Nested
    @DisplayName("When input validation fails")
    class WhenInputValidationFails {
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  \t  "})
        @DisplayName("Should throw exception when ID is null, empty or blank")
        void shouldThrowExceptionWhenIdIsNullOrEmpty(String invalidId) {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> getProductByIdUseCase.getProductById(invalidId));
            assertEquals("Product ID cannot be null or empty", exception.getMessage());
            
            // Verify gateway was never called
            verify(productGateway, never()).getProductById(any());
        }
    }
}
