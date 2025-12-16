package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.dto.InventoryRequest;
import com.ecommerce.inventory.dto.InventoryResponse;
import com.ecommerce.inventory.dto.ProductRequest;
import com.ecommerce.inventory.dto.ProductResponse;
import com.ecommerce.inventory.model.Inventory;
import com.ecommerce.inventory.model.Product;
import com.ecommerce.inventory.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller Integration Tests for InventoryController
 *
 * Testing:
 * - REST endpoints for Product operations
 * - REST endpoints for Inventory operations
 * - HTTP status codes and JSON responses
 */
@WebMvcTest(InventoryController.class)
@DisplayName("InventoryController Integration Tests")
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    private ProductRequest testProductRequest;
    private ProductResponse testProductResponse;
    private InventoryRequest testInventoryRequest;
    private InventoryResponse testInventoryResponse;
    private UUID testProductId;
    private UUID testInventoryId;

    @BeforeEach
    void setUp() {
        testProductId = UUID.randomUUID();
        testInventoryId = UUID.randomUUID();

        // Create test product request
        testProductRequest = new ProductRequest(
                "Laptop",
                "High-performance laptop",
                new BigDecimal("999.99"),
                50
        );

        // Create test product response
        testProductResponse = new ProductResponse();
        testProductResponse.setId(testProductId);
        testProductResponse.setName("Laptop");
        testProductResponse.setDescription("High-performance laptop");
        testProductResponse.setPrice(new BigDecimal("999.99"));
        testProductResponse.setStockQuantity(50);
        testProductResponse.setCreatedAt(LocalDateTime.now());
        testProductResponse.setUpdatedAt(LocalDateTime.now());

        // Create test inventory request
        testInventoryRequest = new InventoryRequest(testProductId, 100, "Warehouse A");

        // Create test inventory response
        testInventoryResponse = new InventoryResponse();
        testInventoryResponse.setId(testInventoryId);
        testInventoryResponse.setProduct(testProductResponse);
        testInventoryResponse.setQuantity(100);
        testInventoryResponse.setLocation("Warehouse A");
        testInventoryResponse.setCreatedAt(LocalDateTime.now());
        testInventoryResponse.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== Product Endpoint Tests ====================

    @Test
    @DisplayName("POST /api/v1/products - Should create product and return 201 CREATED")
    void testCreateProduct_Success() throws Exception {
        // Arrange
        when(inventoryService.createProduct(any(Product.class))).thenReturn(createProductFromRequest());

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProductRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(999.99))
                .andExpect(jsonPath("$.stockQuantity").value(50));

        verify(inventoryService, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - Should return product and 200 OK")
    void testGetProductById_Success() throws Exception {
        // Arrange
        when(inventoryService.getProductById(testProductId)).thenReturn(createProductFromRequest());

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/{id}", testProductId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("High-performance laptop"));

        verify(inventoryService, times(1)).getProductById(testProductId);
    }

    @Test
    @DisplayName("GET /api/v1/products - Should return all products and 200 OK")
    void testGetAllProducts_Success() throws Exception {
        // Arrange
        List<Product> products = List.of(createProductFromRequest());
        when(inventoryService.getAllProducts()).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Laptop"));

        verify(inventoryService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id} - Should update product and return 200 OK")
    void testUpdateProduct_Success() throws Exception {
        // Arrange
        ProductRequest updateRequest = new ProductRequest(
                "Laptop Pro",
                "Updated description",
                new BigDecimal("1299.99"),
                75
        );
        when(inventoryService.updateProduct(eq(testProductId), any(Product.class)))
                .thenReturn(createProductFromRequest());

        // Act & Assert
        mockMvc.perform(put("/api/v1/products/{id}", testProductId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        verify(inventoryService, times(1)).updateProduct(eq(testProductId), any(Product.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id} - Should delete product and return 204 NO CONTENT")
    void testDeleteProduct_Success() throws Exception {
        // Arrange
        doNothing().when(inventoryService).deleteProduct(testProductId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/products/{id}", testProductId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(inventoryService, times(1)).deleteProduct(testProductId);
    }

    @Test
    @DisplayName("GET /api/v1/products/search - Should search products and return 200 OK")
    void testSearchProducts_Success() throws Exception {
        // Arrange
        String searchName = "Laptop";
        List<Product> products = List.of(createProductFromRequest());
        when(inventoryService.searchProductsByName(searchName)).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/search")
                        .param("name", searchName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(inventoryService, times(1)).searchProductsByName(searchName);
    }

    @Test
    @DisplayName("GET /api/v1/products/low-stock - Should return low stock products and 200 OK")
    void testGetLowStockProducts_Success() throws Exception {
        // Arrange
        Integer threshold = 60;
        List<Product> products = List.of(createProductFromRequest());
        when(inventoryService.getLowStockProducts(threshold)).thenReturn(products);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/low-stock")
                        .param("threshold", threshold.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(inventoryService, times(1)).getLowStockProducts(threshold);
    }

    // ==================== Inventory Endpoint Tests ====================

    @Test
    @DisplayName("POST /api/v1/inventory - Should create inventory and return 201 CREATED")
    void testCreateInventory_Success() throws Exception {
        // Arrange
        when(inventoryService.getProductById(testProductId)).thenReturn(createProductFromRequest());
        when(inventoryService.createInventory(any(Inventory.class)))
                .thenReturn(createInventoryFromRequest());

        // Act & Assert
        mockMvc.perform(post("/api/v1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testInventoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(100))
                .andExpect(jsonPath("$.location").value("Warehouse A"));

        verify(inventoryService, times(1)).createInventory(any(Inventory.class));
    }

    @Test
    @DisplayName("GET /api/v1/inventory/{id} - Should return inventory and 200 OK")
    void testGetInventoryById_Success() throws Exception {
        // Arrange
        when(inventoryService.getInventoryById(testInventoryId))
                .thenReturn(createInventoryFromRequest());

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory/{id}", testInventoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(100))
                .andExpect(jsonPath("$.location").value("Warehouse A"));

        verify(inventoryService, times(1)).getInventoryById(testInventoryId);
    }

    @Test
    @DisplayName("GET /api/v1/inventory - Should return all inventory and 200 OK")
    void testGetAllInventory_Success() throws Exception {
        // Arrange
        List<Inventory> inventoryList = List.of(createInventoryFromRequest());
        when(inventoryService.getAllInventory()).thenReturn(inventoryList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(inventoryService, times(1)).getAllInventory();
    }

    @Test
    @DisplayName("PUT /api/v1/inventory/{id} - Should update inventory and return 200 OK")
    void testUpdateInventory_Success() throws Exception {
        // Arrange
        InventoryRequest updateRequest = new InventoryRequest(testProductId, 150, "Warehouse B");
        when(inventoryService.getProductById(testProductId)).thenReturn(createProductFromRequest());
        when(inventoryService.updateInventory(eq(testInventoryId), any(Inventory.class)))
                .thenReturn(createInventoryFromRequest());

        // Act & Assert
        mockMvc.perform(put("/api/v1/inventory/{id}", testInventoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        verify(inventoryService, times(1)).updateInventory(eq(testInventoryId), any(Inventory.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/inventory/{id} - Should delete inventory and return 204 NO CONTENT")
    void testDeleteInventory_Success() throws Exception {
        // Arrange
        doNothing().when(inventoryService).deleteInventory(testInventoryId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/inventory/{id}", testInventoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(inventoryService, times(1)).deleteInventory(testInventoryId);
    }

    @Test
    @DisplayName("GET /api/v1/inventory/location - Should get inventory by location and return 200 OK")
    void testGetInventoryByLocation_Success() throws Exception {
        // Arrange
        String location = "Warehouse A";
        List<Inventory> inventoryList = List.of(createInventoryFromRequest());
        when(inventoryService.getInventoryByLocation(location)).thenReturn(inventoryList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory/location")
                        .param("location", location)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(inventoryService, times(1)).getInventoryByLocation(location);
    }

    @Test
    @DisplayName("PATCH /api/v1/inventory/{id}/stock - Should update stock and return 200 OK")
    void testUpdateStock_Success() throws Exception {
        // Arrange
        Integer newQuantity = 200;
        when(inventoryService.updateStock(testInventoryId, newQuantity))
                .thenReturn(createInventoryFromRequest());

        // Act & Assert
        mockMvc.perform(patch("/api/v1/inventory/{id}/stock", testInventoryId)
                        .param("quantity", newQuantity.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(inventoryService, times(1)).updateStock(testInventoryId, newQuantity);
    }

    @Test
    @DisplayName("POST /api/v1/products - Should return 400 BAD REQUEST for invalid product")
    void testCreateProduct_ValidationFailure() throws Exception {
        // Arrange: Create invalid request (negative price)
        ProductRequest invalidRequest = new ProductRequest();
        invalidRequest.setName(""); // Empty name
        invalidRequest.setPrice(new BigDecimal("-10.00")); // Negative price
        invalidRequest.setStockQuantity(-5); // Negative stock

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(inventoryService, never()).createProduct(any(Product.class));
    }

    // Helper methods
    private Product createProductFromRequest() {
        Product product = new Product(
                testProductRequest.getName(),
                testProductRequest.getDescription(),
                testProductRequest.getPrice(),
                testProductRequest.getStockQuantity()
        );
        product.setId(testProductId);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }

    private Inventory createInventoryFromRequest() {
        Inventory inventory = new Inventory(
                createProductFromRequest(),
                testInventoryRequest.getQuantity(),
                testInventoryRequest.getLocation()
        );
        inventory.setId(testInventoryId);
        inventory.setCreatedAt(LocalDateTime.now());
        inventory.setUpdatedAt(LocalDateTime.now());
        return inventory;
    }
}
