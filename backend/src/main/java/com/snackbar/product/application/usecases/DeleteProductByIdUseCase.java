package com.snackbar.product.application.usecases;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.application.ports.in.DeleteProductByIdInputPort;
import com.snackbar.product.application.ports.out.DomainEventPublisher;
import com.snackbar.product.domain.event.ProductDeletedEvent;

public class DeleteProductByIdUseCase implements DeleteProductByIdInputPort {
    
    private final ProductGateway productGateway;
    private final DomainEventPublisher eventPublisher;

    public DeleteProductByIdUseCase(ProductGateway productGateway, DomainEventPublisher eventPublisher) {
        this.productGateway = productGateway;
        this.eventPublisher = eventPublisher;
    }

    public void deleteProductById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        
        productGateway.deleteProductById(id);
        
        // Publish domain event
        eventPublisher.publish(new ProductDeletedEvent(id));
    }
}
