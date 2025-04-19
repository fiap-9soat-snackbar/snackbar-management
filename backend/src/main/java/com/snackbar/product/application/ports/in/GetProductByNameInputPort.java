package com.snackbar.product.application.ports.in;

import com.snackbar.product.domain.entity.Product;

/**
 * Input port for retrieving a product by its name.
 * This interface defines the contract for the use case of getting a product by name.
 */
public interface GetProductByNameInputPort {
    
    /**
     * Retrieves a product by its name.
     *
     * @param name The name of the product to retrieve
     * @return The product with the specified name
     * @throws IllegalArgumentException if name is null or empty
     */
    Product getProductByName(String name);
}
