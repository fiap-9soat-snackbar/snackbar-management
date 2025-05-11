package com.snackbar.product.application.usecases;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.domain.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class GetProductByCategoryUseCaseTest {

    private ProductGateway productGateway;
    private GetProductByCategoryUseCase getProductByCategoryUseCase;

    @BeforeEach
    void setUp() {
        productGateway = mock(ProductGateway.class);
        getProductByCategoryUseCase = new GetProductByCategoryUseCase(productGateway);
    }

    @Nested
    @DisplayName("When products exist for category")
    class WhenProductsExistForCategory {
        @Test
        @DisplayName("Should return products when category exists")
        void shouldReturnProductsWhenCategoryExists() {
            // Arrange
            String category = "Lanche";
            List<Product> expectedProducts = Arrays.asList(
                new Product("1", "Burger", category, "Delicious burger", BigDecimal.valueOf(10.99), 15),
                new Product("2", "Cheeseburger", category, "Delicious cheeseburger", BigDecimal.valueOf(12.99), 18)
            );
            
            when(productGateway.getProductByCategory(category)).thenReturn(expectedProducts);

            // Act
            List<Product> result = getProductByCategoryUseCase.getProductByCategory(category);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            
            // Detailed assertions for first product
            assertEquals("1", result.get(0).id());
            assertEquals("Burger", result.get(0).name());
            assertEquals("Lanche", result.get(0).category());
            assertEquals("Delicious burger", result.get(0).description());
            assertEquals(BigDecimal.valueOf(10.99), result.get(0).price());
            assertEquals(15, result.get(0).cookingTime());
            
            // Detailed assertions for second product
            assertEquals("2", result.get(1).id());
            assertEquals("Cheeseburger", result.get(1).name());
            assertEquals("Lanche", result.get(1).category());
            assertEquals("Delicious cheeseburger", result.get(1).description());
            assertEquals(BigDecimal.valueOf(12.99), result.get(1).price());
            assertEquals(18, result.get(1).cookingTime());
            
            verify(productGateway, times(1)).getProductByCategory(category);
        }
    }

    @Nested
    @DisplayName("When no products exist for category")
    class WhenNoProductsExistForCategory {
        @Test
        @DisplayName("Should return empty list when no category matches")
        void shouldReturnEmptyListWhenNoCategoryMatches() {
            // Arrange
            String category = "NonExistentCategory";
            when(productGateway.getProductByCategory(category)).thenReturn(Collections.emptyList());

            // Act
            List<Product> result = getProductByCategoryUseCase.getProductByCategory(category);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(productGateway, times(1)).getProductByCategory(category);
        }
    }
    
    @Nested
    @DisplayName("When gateway fails")
    class WhenGatewayFails {
        @Test
        @DisplayName("Should throw exception when gateway fails")
        void shouldThrowExceptionWhenGatewayFails() {
            // Arrange
            String category = "Lanche";
            when(productGateway.getProductByCategory(category)).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> getProductByCategoryUseCase.getProductByCategory(category));
            assertEquals("Database error", exception.getMessage());
            verify(productGateway, times(1)).getProductByCategory(category);
        }
    }
    
    @Nested
    @DisplayName("When input validation fails")
    class WhenInputValidationFails {
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  \t  "})
        @DisplayName("Should throw exception when category is null, empty or blank")
        void shouldThrowExceptionWhenCategoryIsNullOrEmpty(String invalidCategory) {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> getProductByCategoryUseCase.getProductByCategory(invalidCategory));
            assertEquals("Product category cannot be null or empty", exception.getMessage());
            
            // Verify gateway was never called
            verify(productGateway, never()).getProductByCategory(any());
        }
    }
}
