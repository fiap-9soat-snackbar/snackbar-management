package com.snackbar.productv2.application.gateways;

// This should be an abstraction to allow a product to be created, no matter if in
// memory, in a database, or in a file.

import com.snackbar.productv2.domain.entity.Productv2;
import java.util.List;

public interface Productv2Gateway {
    Productv2 createProductv2(Productv2 productv2);
    Productv2 getProductv2ById(String id);
    List<Productv2> listProductsv2();
    List<Productv2> getProductsv2ByCategory(String category);
    Productv2 getProductv2ByName(String name);
    Productv2 updateProductv2ById(String id, Productv2 productv2);
    void deleteProductv2ById(String id);
}
