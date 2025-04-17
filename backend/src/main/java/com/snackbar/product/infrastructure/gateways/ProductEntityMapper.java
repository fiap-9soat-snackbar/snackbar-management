package com.snackbar.product.infrastructure.gateways;

import java.util.List;

import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.infrastructure.persistence.ProductEntity;

public class ProductEntityMapper {
    ProductEntity toEntity(Product productDomainObj) {
        return new ProductEntity(productDomainObj.id (), productDomainObj.name (), productDomainObj.category(), productDomainObj.description(), productDomainObj.price(), productDomainObj.cookingTime());
    }
    
    Product toDomainObj(ProductEntity productEntity) {
        return new Product(productEntity.getId(), productEntity.getName(), productEntity.getCategory(), productEntity.getDescription(), productEntity.getPrice(), productEntity.getCookingTime());
    }

    List<Product> toDomainListObj(List<ProductEntity> productEntityList) {
        return productEntityList.stream()
            .map(this::toDomainObj)
            .toList();
    }

}
