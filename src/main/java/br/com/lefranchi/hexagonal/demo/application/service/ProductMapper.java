package br.com.lefranchi.hexagonal.demo.application.service;

import org.springframework.stereotype.Component;

import br.com.lefranchi.hexagonal.demo.application.port.input.response.ProductResponse;
import br.com.lefranchi.hexagonal.demo.domain.model.Product;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
            .id(product.getId().getValue())
            .name(product.getName())
            .price(product.getPrice().getAmount())
            .status(product.getStatus().name())
            .build();
    }
}