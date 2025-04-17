package com.snackbar.product.application.gateways;

import java.util.List;

import com.snackbar.product.domain.entity.Product;

public interface ProductGateway {
    Product createProduct(Product product);
    Product getProductById(String id);
    List<Product> listProduct();
    List<Product> getProductByCategory(String category);
    Product getProductByName(String name);
    Product updateProductById(String id, Product product);
    void deleteProductById(String id);
}
