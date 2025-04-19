package com.snackbar.product.application.usecases;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.exceptions.ProductNotFoundException;

public class UpdateProductByIdUseCase {
    
    private final ProductGateway productGateway;
    private final GetProductByIdUseCase getProductByIdUseCase;

    public UpdateProductByIdUseCase(ProductGateway productGateway, GetProductByIdUseCase getProductByIdUseCase) {
        this.productGateway = productGateway;
        this.getProductByIdUseCase = getProductByIdUseCase;
    }

    public Product updateProductById(String id, Product product) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        // Verify if product exists
        try {
            getProductByIdUseCase.getProductById(id);
        } catch (ProductNotFoundException e) {
            throw e;
        }
        
        return productGateway.updateProductById(id, product);
    }
}
