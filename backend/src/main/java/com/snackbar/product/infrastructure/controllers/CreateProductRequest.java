package com.snackbar.product.infrastructure.controllers;

import java.math.BigDecimal;

public record CreateProductRequest (String name, String category, String description, BigDecimal price, Integer cookingTime) {
    
    // Compact constructor for validation
    public CreateProductRequest {
        if (name == null) throw new IllegalArgumentException("Name cannot be null");
        if (category == null) throw new IllegalArgumentException("Category cannot be null");
        if (description == null) throw new IllegalArgumentException("Description cannot be null");
        if (price == null) throw new IllegalArgumentException("Price cannot be null");
        if (cookingTime == null) throw new IllegalArgumentException("Cooking time cannot be null");
    }
}
