package com.snackbar.product.infrastructure.gateways;

import java.util.List;

import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.infrastructure.persistence.ProductEntity;

public class ProductEntityMapper {
    public ProductEntity toEntity(Product productDomainObj) {
        if (productDomainObj == null) {
            return null;
        }
        return new ProductEntity(
            productDomainObj.id(), 
            productDomainObj.name(), 
            productDomainObj.category(), 
            productDomainObj.description(), 
            productDomainObj.price(), 
            productDomainObj.cookingTime()
        );
    }
    
    public Product toDomainObj(ProductEntity productEntity) {
        if (productEntity == null) {
            return null;
        }
        return new Product(
            productEntity.getId(), 
            productEntity.getName(), 
            productEntity.getCategory(), 
            productEntity.getDescription(), 
            productEntity.getPrice(), 
            productEntity.getCookingTime()
        );
    }

    public List<Product> toDomainListObj(List<ProductEntity> productEntityList) {
        if (productEntityList == null) {
            return List.of();
        }
        return productEntityList.stream()
            .map(this::toDomainObj)
            .toList();
    }
}
