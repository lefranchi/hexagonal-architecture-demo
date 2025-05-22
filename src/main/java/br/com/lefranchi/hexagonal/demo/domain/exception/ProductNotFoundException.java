package br.com.lefranchi.hexagonal.demo.domain.exception;

import br.com.lefranchi.hexagonal.demo.domain.vo.ProductId;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(ProductId id) {
        super("Product not found with id: " + id.getValue());
    }
}