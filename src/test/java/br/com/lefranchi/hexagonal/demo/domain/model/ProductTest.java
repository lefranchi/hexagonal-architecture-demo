package br.com.lefranchi.hexagonal.demo.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import br.com.lefranchi.hexagonal.demo.domain.exception.InvalidProductException;
import br.com.lefranchi.hexagonal.demo.domain.vo.Money;
import br.com.lefranchi.hexagonal.demo.domain.vo.ProductId;
import br.com.lefranchi.hexagonal.demo.domain.vo.ProductStatus;

class ProductTest {

    @Test
    void shouldCreateActiveProductWithPositivePrice() {
        // Given
        ProductId id = ProductId.generate();
        String name = "Test Product";
        Money price = new Money(10.0);
        
        // When
        Product product = Product.create(id, name, price);
        
        // Then
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(price, product.getPrice());
        assertEquals(ProductStatus.ACTIVE, product.getStatus());
    }
    
    @Test
    void shouldCreateInactiveProductWithNegativePrice() {
        // Given
        ProductId id = ProductId.generate();
        String name = "Test Product";
        Money price = new Money(-10.0);
        
        // When
        Product product = Product.create(id, name, price);
        
        // Then
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(price, product.getPrice());
        assertEquals(ProductStatus.INACTIVE, product.getStatus());
    }
    
    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        // Given
        ProductId id = ProductId.generate();
        String name = "";
        Money price = new Money(10.0);
        
        // When & Then
        assertThrows(InvalidProductException.class, () -> {
            Product.create(id, name, price);
        });
    }
    
    @Test
    void shouldThrowExceptionWhenActivatingProductWithNegativePrice() {
        // Given
        ProductId id = ProductId.generate();
        String name = "Test Product";
        Money price = new Money(-10.0);
        Product product = Product.create(id, name, price);
        
        // When & Then
        assertThrows(InvalidProductException.class, () -> {
            product.activate();
        });
    }
    
    @Test
    void shouldDeactivateProduct() {
        // Given
        ProductId id = ProductId.generate();
        String name = "Test Product";
        Money price = new Money(10.0);
        Product product = Product.create(id, name, price);
        
        // When
        product.deactivate();
        
        // Then
        assertEquals(ProductStatus.INACTIVE, product.getStatus());
    }
    
    @Test
    void shouldUpdateProductName() {
        // Given
        ProductId id = ProductId.generate();
        String name = "Test Product";
        Money price = new Money(10.0);
        Product product = Product.create(id, name, price);
        
        // When
        String newName = "Updated Product";
        product.update(newName, null);
        
        // Then
        assertEquals(newName, product.getName());
        assertEquals(price, product.getPrice());
        assertEquals(ProductStatus.ACTIVE, product.getStatus());
    }
    
    @Test
    void shouldUpdateProductPrice() {
        // Given
        ProductId id = ProductId.generate();
        String name = "Test Product";
        Money price = new Money(10.0);
        Product product = Product.create(id, name, price);
        
        // When
        Money newPrice = new Money(20.0);
        product.update(null, newPrice);
        
        // Then
        assertEquals(name, product.getName());
        assertEquals(newPrice, product.getPrice());
        assertEquals(ProductStatus.ACTIVE, product.getStatus());
    }
    
    @Test
    void shouldUpdateStatusToInactiveWhenPriceBecomesNegative() {
        // Given
        ProductId id = ProductId.generate();
        String name = "Test Product";
        Money price = new Money(10.0);
        Product product = Product.create(id, name, price);
        
        // When
        Money newPrice = new Money(-20.0);
        product.update(null, newPrice);
        
        // Then
        assertEquals(name, product.getName());
        assertEquals(newPrice, product.getPrice());
        assertEquals(ProductStatus.INACTIVE, product.getStatus());
    }
}