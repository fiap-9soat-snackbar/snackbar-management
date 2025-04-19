package com.snackbar.product.domain.event;

import com.snackbar.product.domain.entity.Product;

/**
 * Event that is raised when a product is updated.
 */
public class ProductUpdatedEvent extends DomainEvent {
    private final Product product;
    
    public ProductUpdatedEvent(Product product) {
        super();
        this.product = product;
    }
    
    public Product getProduct() {
        return product;
    }
}
