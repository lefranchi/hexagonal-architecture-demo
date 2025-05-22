package br.com.lefranchi.hexagonal.demo.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.lefranchi.hexagonal.demo.application.port.input.command.CreateProductCommand;
import br.com.lefranchi.hexagonal.demo.application.port.input.command.UpdateProductCommand;
import br.com.lefranchi.hexagonal.demo.application.port.input.response.ProductResponse;
import br.com.lefranchi.hexagonal.demo.application.port.output.ProductEventPublisher;
import br.com.lefranchi.hexagonal.demo.application.port.output.ProductRepository;
import br.com.lefranchi.hexagonal.demo.domain.exception.ProductNotFoundException;
import br.com.lefranchi.hexagonal.demo.domain.model.Product;
import br.com.lefranchi.hexagonal.demo.domain.vo.Money;
import br.com.lefranchi.hexagonal.demo.domain.vo.ProductId;

@ExtendWith(MockitoExtension.class)
class ProductManagementServiceTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private ProductEventPublisher eventPublisher;
    
    @Mock
    private ProductMapper productMapper;
    
    @InjectMocks
    private ProductManagementService service;
    
    private ProductId productId;
    private Product product;
    private ProductResponse productResponse;
    
    @BeforeEach
    void setUp() {
        productId = new ProductId("123");
        product = Product.create(productId, "Test Product", new Money(10.0));
        
        productResponse = ProductResponse.builder()
            .id(productId.getValue())
            .name("Test Product")
            .price(product.getPrice().getAmount())
            .status(product.getStatus().name())
            .build();
    }
    
    @Test
    void shouldCreateProduct() {
        // Given
        CreateProductCommand command = new CreateProductCommand("Test Product", 10.0);
        
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        
        // When
        ProductResponse response = service.createProduct(command);
        
        // Then
        assertNotNull(response);
        assertEquals(productResponse, response);
        
        verify(productRepository).save(any(Product.class));
        verify(eventPublisher).publishProductCreated(any(Product.class));
    }
    
    @Test
    void shouldFindProduct() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        
        // When
        ProductResponse response = service.findProduct(productId);
        
        // Then
        assertNotNull(response);
        assertEquals(productResponse, response);
        
        verify(productRepository).findById(productId);
    }
    
    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ProductNotFoundException.class, () -> {
            service.findProduct(productId);
        });
        
        verify(productRepository).findById(productId);
    }
    
    @Test
    void shouldFindAllProducts() {
        // Given
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        
        // When
        List<ProductResponse> responses = service.findAllProducts();
        
        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(productResponse, responses.get(0));
        
        verify(productRepository).findAll();
    }
    
    @Test
    void shouldUpdateProduct() {
        // Given
        UpdateProductCommand command = new UpdateProductCommand("Updated Product", 20.0);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        
        // When
        ProductResponse response = service.updateProduct(productId, command);
        
        // Then
        assertNotNull(response);
        assertEquals(productResponse, response);
        
        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
        verify(eventPublisher).publishProductUpdated(any(Product.class));
    }
    
    @Test
    void shouldDeleteProduct() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        
        // When
        service.deleteProduct(productId);
        
        // Then
        verify(productRepository).findById(productId);
        verify(productRepository).deleteById(productId);
        verify(eventPublisher).publishProductDeleted(productId);
    }
    
    @Test
    void shouldActivateProduct() {
        // Given
        product.deactivate(); // Ensure it's inactive first
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        
        // When
        ProductResponse response = service.activateProduct(productId);
        
        // Then
        assertNotNull(response);
        assertEquals(productResponse, response);
        
        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
        verify(eventPublisher).publishProductActivated(any(Product.class));
    }
    
    @Test
    void shouldDeactivateProduct() {
        // Given
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        
        // When
        ProductResponse response = service.deactivateProduct(productId);
        
        // Then
        assertNotNull(response);
        assertEquals(productResponse, response);
        
        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
        verify(eventPublisher).publishProductDeactivated(any(Product.class));
    }
}