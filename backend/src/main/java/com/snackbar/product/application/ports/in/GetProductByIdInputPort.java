package com.snackbar.product.application.ports.in;

import com.snackbar.product.domain.entity.Product;

/**
 * Input port for retrieving a product by its ID.
 * This interface defines the contract for the use case of getting a product by ID.
 */
public interface GetProductByIdInputPort {
    
    /**
     * Retrieves a product by its ID.
     *
     * @param id The ID of the product to retrieve
     * @return The product with the specified ID
     * @throws IllegalArgumentException if id is null or empty
     */
    Product getProductById(String id);
}
