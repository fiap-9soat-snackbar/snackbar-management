package com.snackbar.product.application.usecases;

import java.util.List;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.application.ports.in.ListProductInputPort;
import com.snackbar.product.domain.entity.Product;

public class ListProductUseCase implements ListProductInputPort {

    private final ProductGateway productGateway;
    
    public ListProductUseCase(ProductGateway productGateway) {
            this.productGateway = productGateway;
    }

    public List<Product> listProduct() {
        return productGateway.listProduct();
    }
}
