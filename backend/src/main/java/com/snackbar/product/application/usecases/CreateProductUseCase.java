package com.snackbar.product.application.usecases;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.application.ports.in.CreateProductInputPort;
import com.snackbar.product.application.ports.out.DomainEventPublisher;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.event.ProductCreatedEvent;

public class CreateProductUseCase implements CreateProductInputPort {
    
    private final ProductGateway productGateway;
    private final DomainEventPublisher eventPublisher;

    public CreateProductUseCase(ProductGateway productGateway, DomainEventPublisher eventPublisher) {
        this.productGateway = productGateway;
        this.eventPublisher = eventPublisher;
    }

    public Product createProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        Product createdProduct = productGateway.createProduct(product);
        
        // Publish domain event
        eventPublisher.publish(new ProductCreatedEvent(createdProduct));
        
        return createdProduct;
    }
}
