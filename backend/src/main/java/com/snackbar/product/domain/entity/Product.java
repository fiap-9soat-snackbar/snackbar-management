package com.snackbar.product.domain.entity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.snackbar.product.domain.exceptions.InvalidProductDataException;

public record Product(String id, String name, String category, String description, BigDecimal price, Integer cookingTime) {

    private static final List<String> VALID_CATEGORIES = Arrays.asList("Lanche", "Acompanhamento", "Bebida", "Sobremesa");
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MIN_DESCRIPTION_LENGTH = 10;

    // Compact constructor for validation
    public Product {
        validateProduct(name, category, description, price, cookingTime);
    }
    
    // Business rules for product validation
    private static void validateProduct(String name, String category, String description, BigDecimal price, Integer cookingTime) {
        // Name validation
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidProductDataException("Product name is required");
        }
        if (name.trim().length() < MIN_NAME_LENGTH) {
            throw new InvalidProductDataException("Product name must be at least " + MIN_NAME_LENGTH + " characters long");
        }
        
        // Category validation
        if (category == null || category.trim().isEmpty()) {
            throw new InvalidProductDataException("Product category is required");
        }
        if (!VALID_CATEGORIES.contains(category)) {
            throw new InvalidProductDataException("Invalid product category. Must be one of: " + String.join(", ", VALID_CATEGORIES));
        }
        
        // Description validation
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidProductDataException("Product description is required");
        }
        if (description.trim().length() < MIN_DESCRIPTION_LENGTH) {
            throw new InvalidProductDataException("Product description must be at least " + MIN_DESCRIPTION_LENGTH + " characters long");
        }
        
        // Price validation
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidProductDataException("Product price must be greater than zero");
        }
        
        // Cooking time validation
        if (cookingTime == null) {
            throw new InvalidProductDataException("Product cooking time is required");
        }
        if (cookingTime < 0) {
            throw new InvalidProductDataException("Product cooking time must be greater than or equal to zero");
        }
    }
}
