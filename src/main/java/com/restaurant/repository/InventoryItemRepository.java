package com.restaurant.repository;

import com.restaurant.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    
    List<InventoryItem> findByCategory(InventoryItem.Category category);
    
    List<InventoryItem> findByIsLowStockTrue();
    
    List<InventoryItem> findBySupplier(String supplier);
    
    @Query("SELECT ii FROM InventoryItem ii WHERE ii.currentStock <= ii.minimumStock")
    List<InventoryItem> findLowStockItems();
    
    @Query("SELECT ii FROM InventoryItem ii WHERE ii.currentStock <= :threshold")
    List<InventoryItem> findItemsBelowThreshold(@Param("threshold") Double threshold);
    
    @Query("SELECT ii FROM InventoryItem ii WHERE ii.itemName LIKE %:name%")
    List<InventoryItem> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT ii FROM InventoryItem ii WHERE ii.category = :category AND ii.isLowStock = true")
    List<InventoryItem> findLowStockItemsByCategory(@Param("category") InventoryItem.Category category);
    
    @Query("SELECT COUNT(ii) FROM InventoryItem ii WHERE ii.isLowStock = true")
    Long countLowStockItems();
    
    @Query("SELECT ii FROM InventoryItem ii WHERE ii.currentStock <= ii.minimumStock ORDER BY (ii.currentStock / ii.minimumStock) ASC")
    List<InventoryItem> findLowStockItemsOrderedByUrgency();
}
