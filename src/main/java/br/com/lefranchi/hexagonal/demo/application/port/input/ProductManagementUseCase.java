package br.com.lefranchi.hexagonal.demo.application.port.input;

import java.util.List;

import br.com.lefranchi.hexagonal.demo.application.port.input.command.CreateProductCommand;
import br.com.lefranchi.hexagonal.demo.application.port.input.command.UpdateProductCommand;
import br.com.lefranchi.hexagonal.demo.application.port.input.response.ProductResponse;
import br.com.lefranchi.hexagonal.demo.domain.vo.ProductId;

public interface ProductManagementUseCase {
    ProductResponse createProduct(CreateProductCommand command);
    ProductResponse findProduct(ProductId id);
    List<ProductResponse> findAllProducts();
    ProductResponse updateProduct(ProductId id, UpdateProductCommand command);
    void deleteProduct(ProductId id);
    ProductResponse activateProduct(ProductId id);
    ProductResponse deactivateProduct(ProductId id);
}
