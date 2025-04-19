package com.snackbar.product.domain.event;

/**
 * Event that is raised when a product is deleted.
 */
public class ProductDeletedEvent extends DomainEvent {
    private final String productId;
    
    public ProductDeletedEvent(String productId) {
        super();
        this.productId = productId;
    }
    
    public String getProductId() {
        return productId;
    }
}
