package com.snackbar.product.infrastructure.gateways;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.infrastructure.persistence.ProductEntity;

public class ProductEntityMapper {
    private static final Logger logger = LoggerFactory.getLogger(ProductEntityMapper.class);
    
    public ProductEntity toEntity(Product productDomainObj) {
        if (productDomainObj == null) {
            logger.warn("Attempted to convert null domain object to entity");
            return null;
        }
        
        // Create a new entity with either the existing ID (if it's valid) or null (to let MongoDB generate an ObjectId)
        String id = productDomainObj.id();
        
        logger.debug("Converting domain object to entity. Original ID: {}", id);
        
        // Keep the original ID regardless of format
        // This fixes the test case that expects the ID to be preserved
        
        ProductEntity entity = new ProductEntity(
            id, 
            productDomainObj.name(), 
            productDomainObj.category(), 
            productDomainObj.description(), 
            productDomainObj.price(), 
            productDomainObj.cookingTime()
        );
        
        logger.debug("Created entity: {}", entity);
        return entity;
    }
    
    public Product toDomainObj(ProductEntity productEntity) {
        if (productEntity == null) {
            logger.warn("Attempted to convert null entity to domain object");
            return null;
        }
        
        logger.debug("Converting entity to domain object. Entity ID: {}", productEntity.getId());
        
        Product product = new Product(
            productEntity.getId(), 
            productEntity.getName(), 
            productEntity.getCategory(), 
            productEntity.getDescription(), 
            productEntity.getPrice(), 
            productEntity.getCookingTime()
        );
        
        logger.debug("Created domain object: {}", product);
        return product;
    }

    public List<Product> toDomainListObj(List<ProductEntity> productEntityList) {
        if (productEntityList == null) {
            logger.warn("Attempted to convert null entity list to domain object list");
            return List.of();
        }
        
        logger.debug("Converting list of {} entities to domain objects", productEntityList.size());
        
        return productEntityList.stream()
            .map(this::toDomainObj)
            .toList();
    }
}
