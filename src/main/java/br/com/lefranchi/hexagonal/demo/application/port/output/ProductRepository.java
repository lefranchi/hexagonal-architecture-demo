package br.com.lefranchi.hexagonal.demo.application.port.output;

import java.util.List;
import java.util.Optional;

import br.com.lefranchi.hexagonal.demo.domain.model.Product;
import br.com.lefranchi.hexagonal.demo.domain.vo.ProductId;

public interface ProductRepository {
    Optional<Product> findById(ProductId id);
    List<Product> findAll();
    Product save(Product product);
    void deleteById(ProductId id);
}