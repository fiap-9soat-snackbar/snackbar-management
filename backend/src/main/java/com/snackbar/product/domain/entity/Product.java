package com.snackbar.product.domain.entity;

// This is supposed to be a pure DDD entity, not a JPA entity

//import com.snackbar.product.domain.valueobject.ProductId;

import java.math.BigDecimal;

public record Product(String id, String name, String category, String description, BigDecimal price, Integer cookingTime) {

    // Constructor for product creation
    /* public Product(ProductId id, String name, String category, String description, BigDecimal price, Integer cookingTime) {
        this.id = new ProductId();
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.cookingTime = cookingTime;
    } */

    // Business rules for product validation
    /* public void validateProduct() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Product category is required");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }
    } */

} 
