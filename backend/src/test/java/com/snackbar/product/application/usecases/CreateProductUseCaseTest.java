package com.snackbar.product.application.usecases;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.exceptions.InvalidProductDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class CreateProductUseCaseTest {

    private ProductGateway productGateway;
    private CreateProductUseCase createProductUseCase;

    @BeforeEach
    void setUp() {
        productGateway = mock(ProductGateway.class);
        createProductUseCase = new CreateProductUseCase(productGateway);
    }

    @Test
    void shouldCreateProductSuccessfully() {
        // Arrange
        Product product = new Product(null, "Burger", "Lanche", "Delicious burger", BigDecimal.valueOf(10.99), 15);
        Product savedProduct = new Product("1", "Burger", "Lanche", "Delicious burger", BigDecimal.valueOf(10.99), 15);

        when(productGateway.createProduct(product)).thenReturn(savedProduct);

        // Act
        Product result = createProductUseCase.createProduct(product);

        // Assert
        assertNotNull(result);
        assertEquals("1", result.id());
        assertEquals("Burger", result.name());
        verify(productGateway, times(1)).createProduct(product);
    }

    @Test
    void shouldThrowExceptionWhenNameIsInvalid() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class, 
            () -> new Product(null, "Bu", "Lanche", "Delicious burger", BigDecimal.valueOf(10.99), 15));
        assertEquals("Product name must be at least 3 characters long", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCategoryIsInvalid() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class, 
            () -> new Product(null, "Burger", "InvalidCategory", "Delicious burger", BigDecimal.valueOf(10.99), 15));
        assertEquals("Invalid product category. Must be one of: Lanche, Acompanhamento, Bebida, Sobremesa", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsInvalid() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class, 
            () -> new Product(null, "Burger", "Lanche", "Short", BigDecimal.valueOf(10.99), 15));
        assertEquals("Product description must be at least 10 characters long", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPriceIsInvalid() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class, 
            () -> new Product(null, "Burger", "Lanche", "Delicious burger", BigDecimal.valueOf(-1), 15));
        assertEquals("Product price must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCookingTimeIsInvalid() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class, 
            () -> new Product(null, "Burger", "Lanche", "Delicious burger", BigDecimal.valueOf(10.99), -5));
        assertEquals("Product cooking time must be greater than or equal to zero", exception.getMessage());
    }
}