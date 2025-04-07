package com.snackbar.productv2.application.usecases;

import java.util.List;

import com.snackbar.productv2.application.gateways.Productv2Gateway;
import com.snackbar.productv2.domain.entity.Productv2;

public class GetProductsv2ByCategoryUseCase {
    
    private final Productv2Gateway productv2Gateway;

    public GetProductsv2ByCategoryUseCase(Productv2Gateway productv2Gateway) {
        this.productv2Gateway = productv2Gateway;
    }

    public List<Productv2> getProductsv2ByCategory(String category) {
        List<Productv2> retrievedProductv2 = productv2Gateway.getProductsv2ByCategory(category);
        return retrievedProductv2;
    }

}
