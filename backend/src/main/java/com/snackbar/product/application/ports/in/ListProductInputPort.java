package com.snackbar.product.application.ports.in;

import com.snackbar.product.domain.entity.Product;
import java.util.List;

/**
 * Input port for listing all products.
 * This interface defines the contract for the use case of listing all products.
 */
public interface ListProductInputPort {
    
    /**
     * Lists all products.
     *
     * @return A list of all products
     */
    List<Product> listProduct();
}
