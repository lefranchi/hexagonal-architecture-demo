package br.com.lefranchi.hexagonal.demo.infrastructure.output.persistence.mapper;

import br.com.lefranchi.hexagonal.demo.domain.vo.ProductStatus;
import org.springframework.stereotype.Component;

import br.com.lefranchi.hexagonal.demo.domain.model.Product;
import br.com.lefranchi.hexagonal.demo.domain.vo.Money;
import br.com.lefranchi.hexagonal.demo.domain.vo.ProductId;
import br.com.lefranchi.hexagonal.demo.infrastructure.output.persistence.entity.ProductEntity;

@Component
public class ProductEntityMapper {

    public Product toDomain(ProductEntity entity) {
        var product = Product.create(
            new ProductId(entity.getId()),
            entity.getName(),
            new Money(entity.getPrice())
        );
        
        // Ensure status matches the entity status
        if (entity.getStatus() != product.getStatus()) {
            if (entity.getStatus().equals(ProductStatus.ACTIVE)) {
                product.activate();
            } else {
                product.deactivate();
            }
        }
        
        return product;
    }
    
    public ProductEntity toEntity(Product domain) {
        return new ProductEntity(
            domain.getId().getValue(),
            domain.getName(),
            domain.getPrice().getAmount(),
            domain.getStatus()
        );
    }
}