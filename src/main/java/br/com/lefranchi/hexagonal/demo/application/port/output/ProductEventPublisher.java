package br.com.lefranchi.hexagonal.demo.application.port.output;

import br.com.lefranchi.hexagonal.demo.domain.model.Product;
import br.com.lefranchi.hexagonal.demo.domain.vo.ProductId;

public interface ProductEventPublisher {
    void publishProductCreated(Product product);
    void publishProductUpdated(Product product);
    void publishProductDeleted(ProductId id);
    void publishProductActivated(Product product);
    void publishProductDeactivated(Product product);
}