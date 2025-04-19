package com.snackbar.product.infrastructure.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
