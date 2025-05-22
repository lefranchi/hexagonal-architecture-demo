package br.com.lefranchi.hexagonal.demo.domain.vo;

import java.util.Objects;
import java.util.UUID;

public class ProductId {
    private final String value;
    
    public ProductId(String value) {
        this.value = value;
    }
    
    public static ProductId of(String value) {
        return new ProductId(value);
    }
    
    public static ProductId generate() {
        return new ProductId(UUID.randomUUID().toString());
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductId productId = (ProductId) o;
        return Objects.equals(value, productId.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}