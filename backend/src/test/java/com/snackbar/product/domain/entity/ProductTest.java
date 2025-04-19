package com.snackbar.product.domain.entity;

import com.snackbar.product.domain.exceptions.InvalidProductDataException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateProductSuccessfully() {
        // Arrange & Act
        Product product = new Product("1", "Burger", "Lanche", "Delicious burger", BigDecimal.valueOf(10.99), 15);

        // Assert
        assertNotNull(product);
        assertEquals("1", product.id());
        assertEquals("Burger", product.name());
        assertEquals("Lanche", product.category());
        assertEquals("Delicious burger", product.description());
        assertEquals(BigDecimal.valueOf(10.99), product.price());
        assertEquals(15, product.cookingTime());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class,
            () -> new Product("1", null, "Lanche", "Delicious burger", BigDecimal.valueOf(10.99), 15));
        assertEquals("Product name is required", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class,
            () -> new Product("1", "", "Lanche", "Delicious burger", BigDecimal.valueOf(10.99), 15));
        assertEquals("Product name is required", exception.getMessage());
        
        // Also test with whitespace
        exception = assertThrows(InvalidProductDataException.class,
            () -> new Product("1", "   ", "Lanche", "Delicious burger", BigDecimal.valueOf(10.99), 15));
        assertEquals("Product name is required", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNameIsTooShort() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class,
            () -> new Product("1", "Bu", "Lanche", "Delicious burger", BigDecimal.valueOf(10.99), 15));
        assertEquals("Product name must be at least 3 characters long", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCategoryIsNull() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class,
            () -> new Product("1", "Burger", null, "Delicious burger", BigDecimal.valueOf(10.99), 15));
        assertEquals("Product category is required", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenCategoryIsEmpty() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class,
            () -> new Product("1", "Burger", "", "Delicious burger", BigDecimal.valueOf(10.99), 15));
        assertEquals("Product category is required", exception.getMessage());
        
        // Also test with whitespace
        exception = assertThrows(InvalidProductDataException.class,
            () -> new Product("1", "Burger", "   ", "Delicious burger", BigDecimal.valueOf(10.99), 15));
        assertEquals("Product category is required", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCategoryIsInvalid() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class,
            () -> new Product("1", "Burger", "InvalidCategory", "Delicious burger", BigDecimal.valueOf(10.99), 15));
        assertEquals("Invalid product category. Must be one of: Lanche, Acompanhamento, Bebida, Sobremesa", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsNull() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class,
            () -> new Product("1", "Burger", "Lanche", null, BigDecimal.valueOf(10.99), 15));
        assertEquals("Product description is required", exception.getMessage());
    }
    
    @Test
    void shouldThrowExceptionWhenDescriptionIsEmpty() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class,
            () -> new Product("1", "Burger", "Lanche", "", BigDecimal.valueOf(10.99), 15));
        assertEquals("Product description is required", exception.getMessage());
        
        // Also test with whitespace
        exception = assertThrows(InvalidProductDataException.class,
            () -> new Product("1", "Burger", "Lanche", "   ", BigDecimal.valueOf(10.99), 15));
        assertEquals("Product description is required", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsTooShort() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class,
            () -> new Product("1", "Burger", "Lanche", "Short", BigDecimal.valueOf(10.99), 15));
        assertEquals("Product description must be at least 10 characters long", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNull() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class,
            () -> new Product("1", "Burger", "Lanche", "Delicious burger", null, 15));
        assertEquals("Product price must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPriceIsZeroOrNegative() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class,
            () -> new Product("1", "Burger", "Lanche", "Delicious burger", BigDecimal.valueOf(0), 15));
        assertEquals("Product price must be greater than zero", exception.getMessage());

        exception = assertThrows(InvalidProductDataException.class,
            () -> new Product("1", "Burger", "Lanche", "Delicious burger", BigDecimal.valueOf(-1), 15));
        assertEquals("Product price must be greater than zero", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCookingTimeIsNull() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class,
            () -> new Product("1", "Burger", "Lanche", "Delicious burger", BigDecimal.valueOf(10.99), null));
        assertEquals("Product cooking time is required", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCookingTimeIsNegative() {
        // Act & Assert
        InvalidProductDataException exception = assertThrows(InvalidProductDataException.class,
            () -> new Product("1", "Burger", "Lanche", "Delicious burger", BigDecimal.valueOf(10.99), -5));
        assertEquals("Product cooking time must be greater than or equal to zero", exception.getMessage());
    }
    
    @Test
    void shouldAcceptZeroCookingTime() {
        // This is a valid case for products that don't require cooking
        Product product = new Product("1", "Coca-Cola", "Bebida", "Refreshing cola drink", BigDecimal.valueOf(5.99), 0);
        
        assertEquals(0, product.cookingTime());
    }
}