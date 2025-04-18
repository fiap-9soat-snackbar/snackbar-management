package com.snackbar.product.domain.entity;

import java.math.BigDecimal;

public record Product(String id, String name, String category, String description, BigDecimal price, Integer cookingTime) {

    // Compact constructor for validation
    public Product {
        validateProduct(name, category, price);
    }
    
    // Business rules for product validation
    private static void validateProduct(String name, String category, BigDecimal price) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Product category is required");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }
    }
}
