package br.com.lefranchi.hexagonal.demo.infrastructure.output.persistence.adapter;

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

import br.com.lefranchi.hexagonal.demo.domain.model.Product;
import br.com.lefranchi.hexagonal.demo.domain.vo.Money;
import br.com.lefranchi.hexagonal.demo.domain.vo.ProductId;
import br.com.lefranchi.hexagonal.demo.infrastructure.output.persistence.entity.ProductEntity;
import br.com.lefranchi.hexagonal.demo.infrastructure.output.persistence.mapper.ProductEntityMapper;
import br.com.lefranchi.hexagonal.demo.infrastructure.output.persistence.repository.SpringDataProductRepository;

@ExtendWith(MockitoExtension.class)
class JpaProductRepositoryAdapterTest {

    @Mock
    private SpringDataProductRepository repository;
    
    @Mock
    private ProductEntityMapper mapper;
    
    @InjectMocks
    private JpaProductRepositoryAdapter adapter;
    
    private ProductId productId;
    private Product product;
    private ProductEntity entity;
    
    @BeforeEach
    void setUp() {
        productId = new ProductId("123");
        product = Product.create(productId, "Test Product", new Money(10.0));
        
        entity = new ProductEntity();
        entity.setId(productId.getValue());
        entity.setName("Test Product");
        entity.setPrice(product.getPrice().getAmount());
        entity.setStatus(product.getStatus());
    }
    
    @Test
    void shouldFindById() {
        // Given
        when(repository.findById(productId.getValue())).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(product);
        
        // When
        Optional<Product> result = adapter.findById(productId);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(product, result.get());
        
        verify(repository).findById(productId.getValue());
        verify(mapper).toDomain(entity);
    }
    
    @Test
    void shouldReturnEmptyWhenNotFound() {
        // Given
        when(repository.findById(productId.getValue())).thenReturn(Optional.empty());
        
        // When
        Optional<Product> result = adapter.findById(productId);
        
        // Then
        assertFalse(result.isPresent());
        
        verify(repository).findById(productId.getValue());
        verify(mapper, never()).toDomain(any());
    }
    
    @Test
    void shouldFindAll() {
        // Given
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(product);
        
        // When
        List<Product> result = adapter.findAll();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
        
        verify(repository).findAll();
        verify(mapper).toDomain(entity);
    }
    
    @Test
    void shouldSave() {
        // Given
        when(mapper.toEntity(product)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(product);
        
        // When
        Product result = adapter.save(product);
        
        // Then
        assertNotNull(result);
        assertEquals(product, result);
        
        verify(mapper).toEntity(product);
        verify(repository).save(entity);
        verify(mapper).toDomain(entity);
    }
    
    @Test
    void shouldDeleteById() {
        // When
        adapter.deleteById(productId);
        
        // Then
        verify(repository).deleteById(productId.getValue());
    }
}