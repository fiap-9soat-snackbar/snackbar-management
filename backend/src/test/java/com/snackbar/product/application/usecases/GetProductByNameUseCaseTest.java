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

class GetProductByNameUseCaseTest {

    private ProductGateway productGateway;
    private GetProductByNameUseCase getProductByNameUseCase;

    @BeforeEach
    void setUp() {
        productGateway = mock(ProductGateway.class);
        getProductByNameUseCase = new GetProductByNameUseCase(productGateway);
    }

    @Nested
    @DisplayName("When product exists")
    class WhenProductExists {
        @Test
        @DisplayName("Should return product when name exists")
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
    }

    @Nested
    @DisplayName("When product does not exist")
    class WhenProductDoesNotExist {
        @Test
        @DisplayName("Should propagate exception when product name not found")
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
    }
    
    @Nested
    @DisplayName("When gateway fails")
    class WhenGatewayFails {
        @Test
        @DisplayName("Should throw exception when gateway fails")
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
    
    @Nested
    @DisplayName("When input validation fails")
    class WhenInputValidationFails {
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  \t  "})
        @DisplayName("Should throw exception when name is null, empty or blank")
        void shouldThrowExceptionWhenNameIsNullOrEmpty(String invalidName) {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> getProductByNameUseCase.getProductByName(invalidName));
            assertEquals("Product name cannot be null or empty", exception.getMessage());
            
            // Verify gateway was never called
            verify(productGateway, never()).getProductByName(any());
        }
    }
}
