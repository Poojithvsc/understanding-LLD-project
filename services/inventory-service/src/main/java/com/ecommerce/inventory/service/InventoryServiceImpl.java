package com.ecommerce.inventory.service;

import com.ecommerce.inventory.model.Inventory;
import com.ecommerce.inventory.model.Product;
import com.ecommerce.inventory.repository.InventoryRepository;
import com.ecommerce.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryServiceImpl(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    // Product operations
    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product updateProduct(UUID id, Product product) {
        Product existingProduct = getProductById(id);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStockQuantity(product.getStockQuantity());
        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(UUID id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findByStockQuantityLessThan(threshold);
    }

    // Inventory operations
    @Override
    public Inventory createInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory getInventoryById(UUID id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
    }

    @Override
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    @Override
    public Inventory updateInventory(UUID id, Inventory inventory) {
        Inventory existingInventory = getInventoryById(id);
        existingInventory.setQuantity(inventory.getQuantity());
        existingInventory.setLocation(inventory.getLocation());
        return inventoryRepository.save(existingInventory);
    }

    @Override
    public void deleteInventory(UUID id) {
        Inventory inventory = getInventoryById(id);
        inventoryRepository.delete(inventory);
    }

    @Override
    public List<Inventory> getInventoryByLocation(String location) {
        return inventoryRepository.findByLocation(location);
    }

    @Override
    public Inventory updateStock(UUID inventoryId, Integer quantity) {
        Inventory inventory = getInventoryById(inventoryId);
        inventory.setQuantity(quantity);
        return inventoryRepository.save(inventory);
    }
}
