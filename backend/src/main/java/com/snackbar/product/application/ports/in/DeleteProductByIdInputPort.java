package com.snackbar.product.application.ports.in;

/**
 * Input port for deleting a product by its ID.
 * This interface defines the contract for the use case of deleting a product by ID.
 */
public interface DeleteProductByIdInputPort {
    
    /**
     * Deletes a product by its ID.
     *
     * @param id The ID of the product to delete
     * @throws IllegalArgumentException if id is null or empty
     */
    void deleteProductById(String id);
}
