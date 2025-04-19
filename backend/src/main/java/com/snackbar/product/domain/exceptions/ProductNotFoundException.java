package com.snackbar.product.domain.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
    
    public static ProductNotFoundException withId(String id) {
        return new ProductNotFoundException("Product not found with id: " + id);
    }
    
    public static ProductNotFoundException withName(String name) {
        return new ProductNotFoundException("Product not found with name: " + name);
    }
}
