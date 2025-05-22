package br.com.lefranchi.hexagonal.demo.domain.model;

import br.com.lefranchi.hexagonal.demo.domain.exception.InvalidProductException;
import br.com.lefranchi.hexagonal.demo.domain.vo.Money;
import br.com.lefranchi.hexagonal.demo.domain.vo.ProductId;
import br.com.lefranchi.hexagonal.demo.domain.vo.ProductStatus;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    private ProductId id;
    private String name;
    private Money price;
    private ProductStatus status;
    
    private Product(ProductId id, String name, Money price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.status = price.isLessThanZero() ? ProductStatus.INACTIVE : ProductStatus.ACTIVE;
    }
    
    public static Product create(ProductId id, String name, Money price) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidProductException("Product name cannot be empty");
        }
        if (price == null) {
            throw new InvalidProductException("Product price cannot be null");
        }
        
        return new Product(id, name, price);
    }
    
    public void update(String name, Money price) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        
        if (price != null) {
            this.price = price;
            if (this.status == ProductStatus.ACTIVE && price.isLessThanZero()) {
                this.status = ProductStatus.INACTIVE;
            }
        }
    }
    
    public void activate() {
        if (this.price.isLessThanZero()) {
            throw new InvalidProductException("Cannot activate product with negative price");
        }
        this.status = ProductStatus.ACTIVE;
    }
    
    public void deactivate() {
        this.status = ProductStatus.INACTIVE;
    }
}