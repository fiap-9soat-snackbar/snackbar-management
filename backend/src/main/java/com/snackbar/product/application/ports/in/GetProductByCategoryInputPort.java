package com.snackbar.product.application.ports.in;

import com.snackbar.product.domain.entity.Product;
import java.util.List;

/**
 * Input port for retrieving products by category.
 * This interface defines the contract for the use case of getting products by category.
 */
public interface GetProductByCategoryInputPort {
    
    /**
     * Retrieves products by their category.
     *
     * @param category The category to filter products by
     * @return A list of products in the specified category
     * @throws IllegalArgumentException if category is null or empty
     */
    List<Product> getProductByCategory(String category);
}
