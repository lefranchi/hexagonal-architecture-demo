package br.com.lefranchi.hexagonal.demo.infrastructure.output.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.com.lefranchi.hexagonal.demo.application.port.output.ProductEventPublisher;
import br.com.lefranchi.hexagonal.demo.domain.model.Product;
import br.com.lefranchi.hexagonal.demo.domain.vo.ProductId;

@Component
public class LoggingProductEventPublisher implements ProductEventPublisher {
    
    private static final Logger log = LoggerFactory.getLogger(LoggingProductEventPublisher.class);

    @Override
    public void publishProductCreated(Product product) {
        log.info("Product created: {}", product.getId().getValue());
    }

    @Override
    public void publishProductUpdated(Product product) {
        log.info("Product updated: {}", product.getId().getValue());
    }

    @Override
    public void publishProductDeleted(ProductId id) {
        log.info("Product deleted: {}", id.getValue());
    }

    @Override
    public void publishProductActivated(Product product) {
        log.info("Product activated: {}", product.getId().getValue());
    }

    @Override
    public void publishProductDeactivated(Product product) {
        log.info("Product deactivated: {}", product.getId().getValue());
    }
}