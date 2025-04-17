package com.snackbar.product.infrastructure.persistence;

// This should be equivalent to the previous ProductRepositoryImpl, therefore actually having 
// dependencies to a specific framework or library - in this case both Spring and MongoDB.

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<ProductEntity, String> {
    List<ProductEntity> findByCategory(String category);
    Optional<ProductEntity> findByName(String name);
}
