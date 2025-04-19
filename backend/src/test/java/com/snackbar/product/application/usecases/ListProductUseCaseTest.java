package com.snackbar.product.application.usecases;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.domain.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListProductUseCaseTest {

    private ProductGateway productGateway;
    private ListProductUseCase listProductUseCase;

    @BeforeEach
    void setUp() {
        productGateway = mock(ProductGateway.class);
        listProductUseCase = new ListProductUseCase(productGateway);
    }

    @Test
    void shouldReturnListOfProductsSuccessfully() {
        // Arrange
        List<Product> products = Arrays.asList(
            new Product("1", "Burger", "Lanche", "Delicious burger", BigDecimal.valueOf(10.99), 15),
            new Product("2", "Fries", "Acompanhamento", "Crispy fries", BigDecimal.valueOf(5.99), 10)
        );
        when(productGateway.listProduct()).thenReturn(products);

        // Act
        List<Product> result = listProductUseCase.listProduct();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Burger", result.get(0).name());
        assertEquals("Fries", result.get(1).name());
        verify(productGateway, times(1)).listProduct();
    }

    @Test
    void shouldReturnEmptyListWhenNoProductsAvailable() {
        // Arrange
        when(productGateway.listProduct()).thenReturn(Collections.emptyList());

        // Act
        List<Product> result = listProductUseCase.listProduct();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productGateway, times(1)).listProduct();
    }

    @Test
    void shouldThrowExceptionWhenGatewayFails() {
        // Arrange
        when(productGateway.listProduct()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> listProductUseCase.listProduct());
        assertEquals("Database error", exception.getMessage());
        verify(productGateway, times(1)).listProduct();
    }
}