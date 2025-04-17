package com.snackbar.product.application.usecases;

import java.util.List;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.domain.entity.Product;

public class GetProductByCategoryUseCase {
    
    private final ProductGateway productGateway;

    public GetProductByCategoryUseCase(ProductGateway productGateway) {
        this.productGateway = productGateway;
    }

    public List<Product> getProductByCategory(String category) {
        List<Product> retrievedProduct = productGateway.getProductByCategory(category);
        return retrievedProduct;
    }

}
