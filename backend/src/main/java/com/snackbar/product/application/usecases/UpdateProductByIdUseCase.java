package com.snackbar.product.application.usecases;

import com.snackbar.product.application.gateways.ProductGateway;
import com.snackbar.product.application.ports.in.UpdateProductByIdInputPort;
import com.snackbar.product.application.ports.out.DomainEventPublisher;
import com.snackbar.product.domain.entity.Product;
import com.snackbar.product.domain.event.ProductUpdatedEvent;
import com.snackbar.product.domain.exceptions.ProductNotFoundException;

public class UpdateProductByIdUseCase implements UpdateProductByIdInputPort {
    
    private final ProductGateway productGateway;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final DomainEventPublisher eventPublisher;

    public UpdateProductByIdUseCase(
            ProductGateway productGateway, 
            GetProductByIdUseCase getProductByIdUseCase,
            DomainEventPublisher eventPublisher) {
        this.productGateway = productGateway;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.eventPublisher = eventPublisher;
    }

    public Product updateProductById(String id, Product product) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        // Verify if product exists
        try {
            getProductByIdUseCase.getProductById(id);
        } catch (ProductNotFoundException e) {
            throw e;
        }
        
        Product updatedProduct = productGateway.updateProductById(id, product);
        
        // Publish domain event
        eventPublisher.publish(new ProductUpdatedEvent(updatedProduct));
        
        return updatedProduct;
    }
}
