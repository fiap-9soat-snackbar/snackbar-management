package com.snackbar.product.domain.event;

import com.snackbar.product.domain.entity.Product;

/**
 * Event that is raised when a new product is created.
 */
public class ProductCreatedEvent extends DomainEvent {
    private final Product product;
    
    public ProductCreatedEvent(Product product) {
        super();
        this.product = product;
    }
    
    public Product getProduct() {
        return product;
    }
}
