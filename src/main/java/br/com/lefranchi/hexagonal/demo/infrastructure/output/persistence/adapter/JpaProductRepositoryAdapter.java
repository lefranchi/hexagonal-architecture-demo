package br.com.lefranchi.hexagonal.demo.infrastructure.output.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

import br.com.lefranchi.hexagonal.demo.application.port.output.ProductRepository;
import br.com.lefranchi.hexagonal.demo.domain.model.Product;
import br.com.lefranchi.hexagonal.demo.domain.vo.ProductId;
import br.com.lefranchi.hexagonal.demo.infrastructure.output.persistence.mapper.ProductEntityMapper;
import br.com.lefranchi.hexagonal.demo.infrastructure.output.persistence.repository.SpringDataProductRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JpaProductRepositoryAdapter implements ProductRepository {

    private final SpringDataProductRepository repository;
    private final ProductEntityMapper mapper;

    @Override
    public Optional<Product> findById(ProductId id) {
        return repository.findById(id.getValue())
            .map(mapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Product save(Product product) {
        var entity = mapper.toEntity(product);
        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(ProductId id) {
        repository.deleteById(id.getValue());
    }
}