package com.snackbar.product.infrastructure.gateways;

import java.util.List;
import org.bson.types.ObjectId;

import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.infrastructure.persistence.ProductEntity;

public class ProductEntityMapper {
    public ProductEntity toEntity(Product productDomainObj) {
        if (productDomainObj == null) {
            return null;
        }
        
        // Create a new entity with either the existing ID (if it's valid) or null (to let MongoDB generate an ObjectId)
        String id = productDomainObj.id();
        
        // If ID is null or empty, it will be generated as ObjectId by MongoDB
        // If ID is already in ObjectId format, keep it as is
        // If ID is in UUID format, we'll let it be null so MongoDB generates a new ObjectId
        if (id != null && !id.isEmpty() && !isValidObjectId(id)) {
            id = null; // Force MongoDB to generate a new ObjectId
        }
        
        return new ProductEntity(
            id, 
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
    
    // Helper method to check if a string is a valid ObjectId
    private boolean isValidObjectId(String id) {
        try {
            // This will throw IllegalArgumentException if the string is not a valid ObjectId
            new ObjectId(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
