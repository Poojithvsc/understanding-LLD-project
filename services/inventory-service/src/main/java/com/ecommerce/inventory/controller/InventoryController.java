package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.dto.InventoryRequest;
import com.ecommerce.inventory.dto.InventoryResponse;
import com.ecommerce.inventory.dto.ProductRequest;
import com.ecommerce.inventory.dto.ProductResponse;
import com.ecommerce.inventory.model.Inventory;
import com.ecommerce.inventory.model.Product;
import com.ecommerce.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // ==================== Product Endpoints ====================

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStockQuantity()
        );
        Product savedProduct = inventoryService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProductResponse(savedProduct));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        Product product = inventoryService.getProductById(id);
        return ResponseEntity.ok(new ProductResponse(product));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<Product> products = inventoryService.getAllProducts();
        List<ProductResponse> responses = products.stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest request) {
        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStockQuantity()
        );
        Product updatedProduct = inventoryService.updateProduct(id, product);
        return ResponseEntity.ok(new ProductResponse(updatedProduct));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        inventoryService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String name) {
        List<Product> products = inventoryService.searchProductsByName(name);
        List<ProductResponse> responses = products.stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/products/low-stock")
    public ResponseEntity<List<ProductResponse>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        List<Product> products = inventoryService.getLowStockProducts(threshold);
        List<ProductResponse> responses = products.stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // ==================== Inventory Endpoints ====================

    @PostMapping("/inventory")
    public ResponseEntity<InventoryResponse> createInventory(@Valid @RequestBody InventoryRequest request) {
        Product product = inventoryService.getProductById(request.getProductId());
        Inventory inventory = new Inventory(product, request.getQuantity(), request.getLocation());
        Inventory savedInventory = inventoryService.createInventory(inventory);
        return ResponseEntity.status(HttpStatus.CREATED).body(new InventoryResponse(savedInventory));
    }

    @GetMapping("/inventory/{id}")
    public ResponseEntity<InventoryResponse> getInventoryById(@PathVariable UUID id) {
        Inventory inventory = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(new InventoryResponse(inventory));
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        List<Inventory> inventoryList = inventoryService.getAllInventory();
        List<InventoryResponse> responses = inventoryList.stream()
                .map(InventoryResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/inventory/{id}")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable UUID id,
            @Valid @RequestBody InventoryRequest request) {
        Product product = inventoryService.getProductById(request.getProductId());
        Inventory inventory = new Inventory(product, request.getQuantity(), request.getLocation());
        Inventory updatedInventory = inventoryService.updateInventory(id, inventory);
        return ResponseEntity.ok(new InventoryResponse(updatedInventory));
    }

    @DeleteMapping("/inventory/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable UUID id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/inventory/location")
    public ResponseEntity<List<InventoryResponse>> getInventoryByLocation(@RequestParam String location) {
        List<Inventory> inventoryList = inventoryService.getInventoryByLocation(location);
        List<InventoryResponse> responses = inventoryList.stream()
                .map(InventoryResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/inventory/{id}/stock")
    public ResponseEntity<InventoryResponse> updateStock(
            @PathVariable UUID id,
            @RequestParam Integer quantity) {
        Inventory inventory = inventoryService.updateStock(id, quantity);
        return ResponseEntity.ok(new InventoryResponse(inventory));
    }
}
