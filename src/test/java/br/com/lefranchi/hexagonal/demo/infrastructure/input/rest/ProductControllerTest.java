package br.com.lefranchi.hexagonal.demo.infrastructure.input.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.lefranchi.hexagonal.demo.application.port.input.ProductManagementUseCase;
import br.com.lefranchi.hexagonal.demo.application.port.input.response.ProductResponse;
import br.com.lefranchi.hexagonal.demo.domain.exception.InvalidProductException;
import br.com.lefranchi.hexagonal.demo.domain.exception.ProductNotFoundException;
import br.com.lefranchi.hexagonal.demo.domain.vo.ProductId;
import br.com.lefranchi.hexagonal.demo.domain.vo.ProductStatus;
import br.com.lefranchi.hexagonal.demo.infrastructure.input.rest.request.CreateProductRequest;
import br.com.lefranchi.hexagonal.demo.infrastructure.input.rest.request.UpdateProductRequest;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductManagementUseCase productManagement;

    private ProductResponse productResponse;
    private ProductId productId;

    @BeforeEach
    void setUp() {
        productId = new ProductId("123");
        
        productResponse = ProductResponse.builder()
            .id(productId.getValue())
            .name("Test Product")
            .price(BigDecimal.valueOf(10.0))
            .status(ProductStatus.ACTIVE.name())
            .build();
    }

    @Test
    void shouldCreateProduct() throws Exception {
        // Given
        CreateProductRequest request = new CreateProductRequest("Test Product", 10.0);
        when(productManagement.createProduct(any())).thenReturn(productResponse);

        // When/Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(productId.getValue())))
            .andExpect(jsonPath("$.name", is("Test Product")))
            .andExpect(jsonPath("$.price", is(10.0)))
            .andExpect(jsonPath("$.status", is(ProductStatus.ACTIVE.name())));
    }

    @Test
    void shouldReturnBadRequestWhenCreateProductWithInvalidData() throws Exception {
        // Given
        CreateProductRequest request = new CreateProductRequest("", -10.0);
        when(productManagement.createProduct(any())).thenThrow(new InvalidProductException("Invalid product data"));

        // When/Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Invalid product data")));
    }

    @Test
    void shouldGetProduct() throws Exception {
        // Given
        when(productManagement.findProduct(any(ProductId.class))).thenReturn(productResponse);

        // When/Then
        mockMvc.perform(get("/api/products/{id}", productId.getValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(productId.getValue())))
            .andExpect(jsonPath("$.name", is("Test Product")))
            .andExpect(jsonPath("$.price", is(10.0)))
            .andExpect(jsonPath("$.status", is(ProductStatus.ACTIVE.name())));
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        // Given
        when(productManagement.findProduct(any(ProductId.class)))
            .thenThrow(new ProductNotFoundException(productId));

        // When/Then
        mockMvc.perform(get("/api/products/{id}", productId.getValue()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("Product not found with id: " + productId.getValue())));
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        // Given
        ProductResponse anotherProduct = ProductResponse.builder()
            .id("456")
            .name("Another Product")
            .price(BigDecimal.valueOf(20.0))
            .status(ProductStatus.ACTIVE.name())
            .build();
            
        when(productManagement.findAllProducts()).thenReturn(List.of(productResponse, anotherProduct));

        // When/Then
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(productId.getValue())))
            .andExpect(jsonPath("$[0].name", is("Test Product")))
            .andExpect(jsonPath("$[0].price", is(10.0)))
            .andExpect(jsonPath("$[0].status", is(ProductStatus.ACTIVE.name())))
            .andExpect(jsonPath("$[1].id", is("456")))
            .andExpect(jsonPath("$[1].name", is("Another Product")))
            .andExpect(jsonPath("$[1].price", is(20.0)))
            .andExpect(jsonPath("$[1].status", is(ProductStatus.ACTIVE.name())));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        // Given
        UpdateProductRequest request = new UpdateProductRequest("Updated Product", 20.0);
        
        ProductResponse updatedResponse = ProductResponse.builder()
            .id(productId.getValue())
            .name("Updated Product")
            .price(BigDecimal.valueOf(20.0))
            .status(ProductStatus.ACTIVE.name())
            .build();
            
        when(productManagement.updateProduct(any(ProductId.class), any())).thenReturn(updatedResponse);

        // When/Then
        mockMvc.perform(put("/api/products/{id}", productId.getValue())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(productId.getValue())))
            .andExpect(jsonPath("$.name", is("Updated Product")))
            .andExpect(jsonPath("$.price", is(20.0)))
            .andExpect(jsonPath("$.status", is(ProductStatus.ACTIVE.name())));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        // When/Then
        mockMvc.perform(delete("/api/products/{id}", productId.getValue()))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentProduct() throws Exception {
        // Given
        doThrow(new ProductNotFoundException(productId))
            .when(productManagement).deleteProduct(any(ProductId.class));

        // When/Then
        mockMvc.perform(delete("/api/products/{id}", productId.getValue()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("Product not found with id: " + productId.getValue())));
    }

    @Test
    void shouldActivateProduct() throws Exception {
        // Given
        when(productManagement.activateProduct(any(ProductId.class))).thenReturn(productResponse);

        // When/Then
        mockMvc.perform(patch("/api/products/{id}/activate", productId.getValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(productId.getValue())))
            .andExpect(jsonPath("$.name", is("Test Product")))
            .andExpect(jsonPath("$.price", is(10.0)))
            .andExpect(jsonPath("$.status", is(ProductStatus.ACTIVE.name())));
    }

    @Test
    void shouldReturnBadRequestWhenActivatingProductWithNegativePrice() throws Exception {
        // Given
        when(productManagement.activateProduct(any(ProductId.class)))
            .thenThrow(new InvalidProductException("Cannot activate product with negative price"));

        // When/Then
        mockMvc.perform(patch("/api/products/{id}/activate", productId.getValue()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Cannot activate product with negative price")));
    }

    @Test
    void shouldDeactivateProduct() throws Exception {
        // Given
        ProductResponse deactivatedResponse = ProductResponse.builder()
            .id(productId.getValue())
            .name("Test Product")
            .price(BigDecimal.valueOf(10.0))
            .status(ProductStatus.INACTIVE.name())
            .build();
            
        when(productManagement.deactivateProduct(any(ProductId.class))).thenReturn(deactivatedResponse);

        // When/Then
        mockMvc.perform(patch("/api/products/{id}/deactivate", productId.getValue()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(productId.getValue())))
            .andExpect(jsonPath("$.name", is("Test Product")))
            .andExpect(jsonPath("$.price", is(10.0)))
            .andExpect(jsonPath("$.status", is(ProductStatus.INACTIVE.name())));
    }
}