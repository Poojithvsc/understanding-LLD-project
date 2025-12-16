package com.ecommerce.inventory.service;

import com.ecommerce.inventory.model.Inventory;
import com.ecommerce.inventory.model.Product;

import java.util.List;
import java.util.UUID;

public interface InventoryService {

    // Product operations
    Product createProduct(Product product);
    Product getProductById(UUID id);
    List<Product> getAllProducts();
    Product updateProduct(UUID id, Product product);
    void deleteProduct(UUID id);
    List<Product> searchProductsByName(String name);
    List<Product> getLowStockProducts(Integer threshold);

    // Inventory operations
    Inventory createInventory(Inventory inventory);
    Inventory getInventoryById(UUID id);
    List<Inventory> getAllInventory();
    Inventory updateInventory(UUID id, Inventory inventory);
    void deleteInventory(UUID id);
    List<Inventory> getInventoryByLocation(String location);
    Inventory updateStock(UUID inventoryId, Integer quantity);
}
