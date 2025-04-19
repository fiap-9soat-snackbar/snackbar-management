package com.snackbar.product.infrastructure.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateProductResponseTest {

    @Test
    @DisplayName("Should create CreateProductResponse with all properties")
    void constructor_ShouldCreateCreateProductResponseWithAllProperties() {
        // Given
        String id = "1";
        String name = "Test Product";
        String category = "Lanche";
        String description = "Test description for product";
        BigDecimal price = new BigDecimal("10.99");
        Integer cookingTime = 5;

        // When
        CreateProductResponse response = new CreateProductResponse(id, name, category, description, price, cookingTime);

        // Then
        assertEquals(id, response.id());
        assertEquals(name, response.name());
        assertEquals(category, response.category());
        assertEquals(description, response.description());
        assertEquals(price, response.price());
        assertEquals(cookingTime, response.cookingTime());
    }
}
