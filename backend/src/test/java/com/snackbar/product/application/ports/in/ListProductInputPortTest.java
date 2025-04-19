package com.snackbar.product.application.ports.in;

import com.snackbar.product.domain.entity.Product;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListProductInputPortTest {

    // Test implementation of ListProductInputPort for testing
    private static class TestListProductInputPort implements ListProductInputPort {
        private boolean wasCalled = false;
        
        @Override
        public List<Product> listProduct() {
            this.wasCalled = true;
            
            // Return mock products
            List<Product> products = new ArrayList<>();
            products.add(new Product("1", "Product 1", "Lanche", "Description 1", new BigDecimal("10.0"), 5));
            products.add(new Product("2", "Product 2", "Bebida", "Description 2", new BigDecimal("20.0"), 10));
            products.add(new Product("3", "Product 3", "Sobremesa", "Description 3", new BigDecimal("15.0"), 7));
            
            return products;
        }
        
        public boolean wasCalled() {
            return wasCalled;
        }
    }

    @Test
    void shouldListProducts() {
        // Arrange
        TestListProductInputPort port = new TestListProductInputPort();
        
        // Act
        List<Product> products = port.listProduct();
        
        // Assert
        assertTrue(port.wasCalled());
        assertNotNull(products);
        assertEquals(3, products.size());
        
        // Verify first product
        assertEquals("1", products.get(0).id());
        assertEquals("Product 1", products.get(0).name());
        assertEquals("Lanche", products.get(0).category());
        
        // Verify second product
        assertEquals("2", products.get(1).id());
        assertEquals("Product 2", products.get(1).name());
        assertEquals("Bebida", products.get(1).category());
        
        // Verify third product
        assertEquals("3", products.get(2).id());
        assertEquals("Product 3", products.get(2).name());
        assertEquals("Sobremesa", products.get(2).category());
    }
}
