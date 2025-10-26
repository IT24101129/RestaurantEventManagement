package com.banquet.management.service;

import com.banquet.management.model.Inventory;
import com.banquet.management.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    public List<Inventory> getAllInventoryItems() {
        return inventoryRepository.findAllInventoryItems();
    }

    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }

    public List<Inventory> getOutOfStockItems() {
        return inventoryRepository.findOutOfStockItems();
    }

    public boolean updateItemQuantity(Integer itemId, Integer newQuantity) {
        try {
            // Validate input
            if (itemId == null || newQuantity == null || newQuantity < 0) {
                return false;
            }

            Inventory existingItem = inventoryRepository.findInventoryItemById(itemId);
            if (existingItem == null) {
                return false;
            }

            int rowsAffected = inventoryRepository.updateInventoryQuantity(itemId, newQuantity);
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error updating item quantity: " + e.getMessage());
            return false;
        }
    }

    public boolean restockItem(Integer itemId, Integer quantityToAdd) {
        try {
            // Validate input
            if (itemId == null || quantityToAdd == null || quantityToAdd <= 0) {
                return false;
            }

            Inventory existingItem = inventoryRepository.findInventoryItemById(itemId);
            if (existingItem == null) {
                return false;
            }

            int rowsAffected = inventoryRepository.restockInventoryItem(itemId, quantityToAdd);
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error restocking item: " + e.getMessage());
            return false;
        }
    }

    public boolean addNewItem(Inventory inventory) {
        try {
            // Validate input
            if (inventory == null || inventory.getName() == null || inventory.getName().trim().isEmpty()) {
                return false;
            }

            int rowsAffected = inventoryRepository.addNewInventoryItem(inventory);
            return rowsAffected > 0;
        } catch (Exception e) {
            System.err.println("Error adding new item: " + e.getMessage());
            return false;
        }
    }

    // Get inventory summary statistics
    public InventorySummary getInventorySummary() {
        List<Inventory> allItems = getAllInventoryItems();
        long totalItems = allItems.size();
        long lowStockItems = allItems.stream().filter(Inventory::needsRestocking).count();
        long outOfStockItems = allItems.stream().filter(item -> item.getQuantity() == 0).count();
        long inStockItems = totalItems - lowStockItems - outOfStockItems;

        return new InventorySummary(totalItems, inStockItems, lowStockItems, outOfStockItems);
    }

    // Inner class for inventory summary
    public static class InventorySummary {
        private final long totalItems;
        private final long inStockItems;
        private final long lowStockItems;
        private final long outOfStockItems;

        public InventorySummary(long totalItems, long inStockItems, long lowStockItems, long outOfStockItems) {
            this.totalItems = totalItems;
            this.inStockItems = inStockItems;
            this.lowStockItems = lowStockItems;
            this.outOfStockItems = outOfStockItems;
        }

        // Getters
        public long getTotalItems() { return totalItems; }
        public long getInStockItems() { return inStockItems; }
        public long getLowStockItems() { return lowStockItems; }
        public long getOutOfStockItems() { return outOfStockItems; }
    }
}