package br.com.lefranchi.hexagonal.demo.infrastructure.input.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.lefranchi.hexagonal.demo.infrastructure.input.rest.request.CreateProductRequest;
import br.com.lefranchi.hexagonal.demo.infrastructure.input.rest.request.UpdateProductRequest;
import br.com.lefranchi.hexagonal.demo.infrastructure.output.persistence.repository.SpringDataProductRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringDataProductRepository productRepository;

    @Test
    void shouldCreateAndRetrieveProduct() throws Exception {
        // First, create a product
        CreateProductRequest createRequest = new CreateProductRequest("Test Product", 25.99);
        
        String productJson = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name", is("Test Product")))
            .andExpect(jsonPath("$.price", is(25.99)))
            .andExpect(jsonPath("$.status", is("ACTIVE")))
            .andReturn()
            .getResponse()
            .getContentAsString();
        
        // Extract the ID from the response
        String productId = objectMapper.readTree(productJson).get("id").asText();
        
        // Then, retrieve the product
        mockMvc.perform(get("/api/products/{id}", productId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(productId)))
            .andExpect(jsonPath("$.name", is("Test Product")))
            .andExpect(jsonPath("$.price", is(25.99)))
            .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    void shouldReturnAllProducts() throws Exception {
        // Create a couple of products first
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateProductRequest("Product 1", 10.0))))
            .andExpect(status().isCreated());
            
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateProductRequest("Product 2", 20.0))))
            .andExpect(status().isCreated());
        
        // Then check the list endpoint
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", is("Product 1")))
            .andExpect(jsonPath("$[1].name", is("Product 2")));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        // Create a product first
        String productJson = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateProductRequest("Original Name", 15.0))))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
        
        String productId = objectMapper.readTree(productJson).get("id").asText();
        
        // Update the product
        UpdateProductRequest updateRequest = new UpdateProductRequest("Updated Name", 25.0);
        
        mockMvc.perform(put("/api/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(productId)))
            .andExpect(jsonPath("$.name", is("Updated Name")))
            .andExpect(jsonPath("$.price", is(25.0)))
            .andExpect(jsonPath("$.status", is("ACTIVE")));
        
        // Verify the update persisted
        mockMvc.perform(get("/api/products/{id}", productId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("Updated Name")))
            .andExpect(jsonPath("$.price", is(25.0)));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        // Create a product first
        String productJson = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateProductRequest("Product to Delete", 30.0))))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
        
        String productId = objectMapper.readTree(productJson).get("id").asText();
        
        // Delete the product
        mockMvc.perform(delete("/api/products/{id}", productId))
            .andExpect(status().isNoContent());
        
        // Verify it's deleted
        mockMvc.perform(get("/api/products/{id}", productId))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldActivateAndDeactivateProduct() throws Exception {
        // Create a product first
        String productJson = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateProductRequest("Product to Toggle", 40.0))))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
        
        String productId = objectMapper.readTree(productJson).get("id").asText();
        
        // Deactivate the product
        mockMvc.perform(patch("/api/products/{id}/deactivate", productId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("INACTIVE")));
        
        // Verify it's deactivated
        mockMvc.perform(get("/api/products/{id}", productId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("INACTIVE")));
        
        // Activate the product again
        mockMvc.perform(patch("/api/products/{id}/activate", productId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("ACTIVE")));
        
        // Verify it's activated
        mockMvc.perform(get("/api/products/{id}", productId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    void shouldFailToActivateProductWithNegativePrice() throws Exception {
        // Create a product with negative price
        String productJson = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateProductRequest("Negative Price Product", -10.0))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status", is("INACTIVE"))) // Should be created as inactive
            .andReturn()
            .getResponse()
            .getContentAsString();
        
        String productId = objectMapper.readTree(productJson).get("id").asText();
        
        // Try to activate it, should fail
        mockMvc.perform(patch("/api/products/{id}/activate", productId))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Cannot activate product with negative price")));
    }
}