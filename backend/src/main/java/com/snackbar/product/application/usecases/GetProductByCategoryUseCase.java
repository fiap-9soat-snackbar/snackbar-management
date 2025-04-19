package com.snackbar.product.application.usecases;

import java.util.List;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.application.ports.in.GetProductByCategoryInputPort;
import com.snackbar.product.domain.entity.Product;

public class GetProductByCategoryUseCase implements GetProductByCategoryInputPort {
    
    private final ProductGateway productGateway;

    public GetProductByCategoryUseCase(ProductGateway productGateway) {
        this.productGateway = productGateway;
    }

    public List<Product> getProductByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Product category cannot be null or empty");
        }
        return productGateway.getProductByCategory(category);
    }
}
