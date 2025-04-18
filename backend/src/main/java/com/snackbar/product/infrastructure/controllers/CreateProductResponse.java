package com.snackbar.product.infrastructure.controllers;

import java.math.BigDecimal;

public record CreateProductResponse (String id, String name, String category, String description, BigDecimal price, Integer cookingTime) {
    
}
