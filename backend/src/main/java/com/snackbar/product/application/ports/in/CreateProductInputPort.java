package com.snackbar.product.application.ports.in;

import com.snackbar.product.domain.entity.Product;

/**
 * Input port for creating a product.
 * This interface defines the contract for the use case of creating a product.
 */
public interface CreateProductInputPort {
    
    /**
     * Creates a new product.
     *
     * @param product The product to be created
     * @return The created product with generated ID
     * @throws IllegalArgumentException if product is null
     */
    Product createProduct(Product product);
}
