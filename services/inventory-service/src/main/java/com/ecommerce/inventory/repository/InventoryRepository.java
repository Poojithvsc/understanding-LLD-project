package com.ecommerce.inventory.repository;

import com.ecommerce.inventory.model.Inventory;
import com.ecommerce.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    List<Inventory> findByProduct(Product product);

    List<Inventory> findByLocation(String location);

    Optional<Inventory> findByProductAndLocation(Product product, String location);
}
