package com.snackbar.product.infrastructure.controllers;

import java.math.BigDecimal;

public record GetProductResponse (String id, String name, String category, String description, BigDecimal price, Integer cookingTime) {
    
}
