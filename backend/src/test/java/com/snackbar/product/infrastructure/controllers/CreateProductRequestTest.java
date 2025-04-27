package com.snackbar.product.infrastructure.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateProductRequestTest {

    @Test
    @DisplayName("Should create CreateProductRequest with all properties")
    void constructor_ShouldCreateCreateProductRequestWithAllProperties() {
        // Given
        String name = "Test Product";
        String category = "Lanche";
        String description = "Test description for product";
        BigDecimal price = new BigDecimal("10.99");
        Integer cookingTime = 5;

        // When
        CreateProductRequest request = new CreateProductRequest(name, category, description, price, cookingTime);

        // Then
        assertEquals(name, request.name());
        assertEquals(category, request.category());
        assertEquals(description, request.description());
        assertEquals(price, request.price());
        assertEquals(cookingTime, request.cookingTime());
    }
    
    @Test
    @DisplayName("Should throw exception when name is null")
    void constructor_ShouldThrowExceptionWhenNameIsNull() {
        // Given
        String name = null;
        String category = "Lanche";
        String description = "Test description for product";
        BigDecimal price = new BigDecimal("10.99");
        Integer cookingTime = 5;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProductRequest(name, category, description, price, cookingTime);
        });
        
        assertEquals("Name cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should throw exception when category is null")
    void constructor_ShouldThrowExceptionWhenCategoryIsNull() {
        // Given
        String name = "Test Product";
        String category = null;
        String description = "Test description for product";
        BigDecimal price = new BigDecimal("10.99");
        Integer cookingTime = 5;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProductRequest(name, category, description, price, cookingTime);
        });
        
        assertEquals("Category cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should throw exception when description is null")
    void constructor_ShouldThrowExceptionWhenDescriptionIsNull() {
        // Given
        String name = "Test Product";
        String category = "Lanche";
        String description = null;
        BigDecimal price = new BigDecimal("10.99");
        Integer cookingTime = 5;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProductRequest(name, category, description, price, cookingTime);
        });
        
        assertEquals("Description cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should throw exception when price is null")
    void constructor_ShouldThrowExceptionWhenPriceIsNull() {
        // Given
        String name = "Test Product";
        String category = "Lanche";
        String description = "Test description for product";
        BigDecimal price = null;
        Integer cookingTime = 5;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProductRequest(name, category, description, price, cookingTime);
        });
        
        assertEquals("Price cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should throw exception when cookingTime is null")
    void constructor_ShouldThrowExceptionWhenCookingTimeIsNull() {
        // Given
        String name = "Test Product";
        String category = "Lanche";
        String description = "Test description for product";
        BigDecimal price = new BigDecimal("10.99");
        Integer cookingTime = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProductRequest(name, category, description, price, cookingTime);
        });
        
        assertEquals("Cooking time cannot be null", exception.getMessage());
    }
}
