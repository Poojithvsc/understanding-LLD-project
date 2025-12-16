package com.ecommerce.inventory.service;

import com.ecommerce.inventory.model.Inventory;
import com.ecommerce.inventory.model.Product;
import com.ecommerce.inventory.repository.InventoryRepository;
import com.ecommerce.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InventoryServiceImpl
 *
 * Testing:
 * - Product operations (CRUD)
 * - Inventory operations (CRUD)
 * - Business logic with mocked repositories
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryServiceImpl Unit Tests")
class InventoryServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Product testProduct;
    private Inventory testInventory;
    private UUID testProductId;
    private UUID testInventoryId;

    @BeforeEach
    void setUp() {
        testProductId = UUID.randomUUID();
        testInventoryId = UUID.randomUUID();

        // Create test product
        testProduct = new Product("Laptop", "High-performance laptop", new BigDecimal("999.99"), 50);
        testProduct.setId(testProductId);
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());

        // Create test inventory
        testInventory = new Inventory(testProduct, 100, "Warehouse A");
        testInventory.setId(testInventoryId);
        testInventory.setCreatedAt(LocalDateTime.now());
        testInventory.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== Product Tests ====================

    @Test
    @DisplayName("Should create product successfully")
    void testCreateProduct_Success() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        Product result = inventoryService.createProduct(testProduct);

        // Assert
        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(new BigDecimal("999.99"), result.getPrice());
        assertEquals(50, result.getStockQuantity());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should get product by ID successfully")
    void testGetProductById_Success() {
        // Arrange
        when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));

        // Act
        Product result = inventoryService.getProductById(testProductId);

        // Assert
        assertNotNull(result);
        assertEquals(testProductId, result.getId());
        assertEquals("Laptop", result.getName());
        verify(productRepository, times(1)).findById(testProductId);
    }

    @Test
    @DisplayName("Should throw exception when product not found by ID")
    void testGetProductById_NotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.getProductById(nonExistentId);
        });

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(productRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should get all products successfully")
    void testGetAllProducts_Success() {
        // Arrange
        List<Product> products = List.of(testProduct);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = inventoryService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update product successfully")
    void testUpdateProduct_Success() {
        // Arrange
        Product updatedProduct = new Product("Laptop Pro", "Updated description", new BigDecimal("1299.99"), 75);
        when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        Product result = inventoryService.updateProduct(testProductId, updatedProduct);

        // Assert
        assertNotNull(result);
        verify(productRepository, times(1)).findById(testProductId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should delete product successfully")
    void testDeleteProduct_Success() {
        // Arrange
        when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(testProduct);

        // Act
        inventoryService.deleteProduct(testProductId);

        // Assert
        verify(productRepository, times(1)).findById(testProductId);
        verify(productRepository, times(1)).delete(testProduct);
    }

    @Test
    @DisplayName("Should search products by name successfully")
    void testSearchProductsByName_Success() {
        // Arrange
        String searchName = "Laptop";
        List<Product> products = List.of(testProduct);
        when(productRepository.findByNameContainingIgnoreCase(searchName)).thenReturn(products);

        // Act
        List<Product> result = inventoryService.searchProductsByName(searchName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        verify(productRepository, times(1)).findByNameContainingIgnoreCase(searchName);
    }

    @Test
    @DisplayName("Should get low stock products successfully")
    void testGetLowStockProducts_Success() {
        // Arrange
        Integer threshold = 60;
        List<Product> products = List.of(testProduct);
        when(productRepository.findByStockQuantityLessThan(threshold)).thenReturn(products);

        // Act
        List<Product> result = inventoryService.getLowStockProducts(threshold);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findByStockQuantityLessThan(threshold);
    }

    // ==================== Inventory Tests ====================

    @Test
    @DisplayName("Should create inventory successfully")
    void testCreateInventory_Success() {
        // Arrange
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        // Act
        Inventory result = inventoryService.createInventory(testInventory);

        // Assert
        assertNotNull(result);
        assertEquals(100, result.getQuantity());
        assertEquals("Warehouse A", result.getLocation());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Should get inventory by ID successfully")
    void testGetInventoryById_Success() {
        // Arrange
        when(inventoryRepository.findById(testInventoryId)).thenReturn(Optional.of(testInventory));

        // Act
        Inventory result = inventoryService.getInventoryById(testInventoryId);

        // Assert
        assertNotNull(result);
        assertEquals(testInventoryId, result.getId());
        assertEquals(100, result.getQuantity());
        verify(inventoryRepository, times(1)).findById(testInventoryId);
    }

    @Test
    @DisplayName("Should throw exception when inventory not found by ID")
    void testGetInventoryById_NotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(inventoryRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.getInventoryById(nonExistentId);
        });

        assertTrue(exception.getMessage().contains("Inventory not found"));
        verify(inventoryRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should get all inventory successfully")
    void testGetAllInventory_Success() {
        // Arrange
        List<Inventory> inventoryList = List.of(testInventory);
        when(inventoryRepository.findAll()).thenReturn(inventoryList);

        // Act
        List<Inventory> result = inventoryService.getAllInventory();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Warehouse A", result.get(0).getLocation());
        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update inventory successfully")
    void testUpdateInventory_Success() {
        // Arrange
        Inventory updatedInventory = new Inventory(testProduct, 150, "Warehouse B");
        when(inventoryRepository.findById(testInventoryId)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        // Act
        Inventory result = inventoryService.updateInventory(testInventoryId, updatedInventory);

        // Assert
        assertNotNull(result);
        verify(inventoryRepository, times(1)).findById(testInventoryId);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Should delete inventory successfully")
    void testDeleteInventory_Success() {
        // Arrange
        when(inventoryRepository.findById(testInventoryId)).thenReturn(Optional.of(testInventory));
        doNothing().when(inventoryRepository).delete(testInventory);

        // Act
        inventoryService.deleteInventory(testInventoryId);

        // Assert
        verify(inventoryRepository, times(1)).findById(testInventoryId);
        verify(inventoryRepository, times(1)).delete(testInventory);
    }

    @Test
    @DisplayName("Should get inventory by location successfully")
    void testGetInventoryByLocation_Success() {
        // Arrange
        String location = "Warehouse A";
        List<Inventory> inventoryList = List.of(testInventory);
        when(inventoryRepository.findByLocation(location)).thenReturn(inventoryList);

        // Act
        List<Inventory> result = inventoryService.getInventoryByLocation(location);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(location, result.get(0).getLocation());
        verify(inventoryRepository, times(1)).findByLocation(location);
    }

    @Test
    @DisplayName("Should update stock quantity successfully")
    void testUpdateStock_Success() {
        // Arrange
        Integer newQuantity = 200;
        when(inventoryRepository.findById(testInventoryId)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        // Act
        Inventory result = inventoryService.updateStock(testInventoryId, newQuantity);

        // Assert
        assertNotNull(result);
        verify(inventoryRepository, times(1)).findById(testInventoryId);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }
}
