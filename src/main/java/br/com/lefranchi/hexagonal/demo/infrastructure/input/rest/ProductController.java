package br.com.lefranchi.hexagonal.demo.infrastructure.input.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.lefranchi.hexagonal.demo.application.port.input.ProductManagementUseCase;
import br.com.lefranchi.hexagonal.demo.application.port.input.command.CreateProductCommand;
import br.com.lefranchi.hexagonal.demo.application.port.input.command.UpdateProductCommand;
import br.com.lefranchi.hexagonal.demo.application.port.input.response.ProductResponse;
import br.com.lefranchi.hexagonal.demo.domain.vo.ProductId;
import br.com.lefranchi.hexagonal.demo.infrastructure.input.rest.request.CreateProductRequest;
import br.com.lefranchi.hexagonal.demo.infrastructure.input.rest.request.UpdateProductRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductManagementUseCase productManagement;
    
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        CreateProductCommand command = new CreateProductCommand(
            request.getName(), 
            request.getPrice()
        );
        
        ProductResponse response = productManagement.createProduct(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String id) {
        ProductId productId = new ProductId(id);
        ProductResponse response = productManagement.findProduct(productId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> responses = productManagement.findAllProducts();
        return ResponseEntity.ok(responses);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @RequestBody UpdateProductRequest request) {
        
        ProductId productId = new ProductId(id);
        UpdateProductCommand command = new UpdateProductCommand(
            request.getName(),
            request.getPrice()
        );
        
        ProductResponse response = productManagement.updateProduct(productId, command);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        ProductId productId = new ProductId(id);
        productManagement.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ProductResponse> activateProduct(@PathVariable String id) {
        ProductId productId = new ProductId(id);
        ProductResponse response = productManagement.activateProduct(productId);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ProductResponse> deactivateProduct(@PathVariable String id) {
        ProductId productId = new ProductId(id);
        ProductResponse response = productManagement.deactivateProduct(productId);
        return ResponseEntity.ok(response);
    }
}