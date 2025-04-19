package com.snackbar.product.application.ports.in;

import com.snackbar.product.domain.entity.Product;

/**
 * Input port for updating a product by its ID.
 * This interface defines the contract for the use case of updating a product by ID.
 */
public interface UpdateProductByIdInputPort {
    
    /**
     * Updates a product by its ID.
     *
     * @param id The ID of the product to update
     * @param product The updated product data
     * @return The updated product
     * @throws IllegalArgumentException if id is null or empty, or if product is null
     */
    Product updateProductById(String id, Product product);
}
