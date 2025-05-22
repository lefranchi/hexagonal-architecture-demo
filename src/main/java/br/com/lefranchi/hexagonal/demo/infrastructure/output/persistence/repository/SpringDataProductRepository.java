package br.com.lefranchi.hexagonal.demo.infrastructure.output.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import br.com.lefranchi.hexagonal.demo.infrastructure.output.persistence.entity.ProductEntity;

@Repository
public interface SpringDataProductRepository extends CrudRepository<ProductEntity, String> {
}