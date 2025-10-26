package com.banquet.management.model;

import java.time.LocalDate;

public class Inventory {
    private Integer inventoryItemId;
    private String name;
    private Integer quantity;
    private LocalDate lastRestockDate;
    private Integer reorderLevel;

    // Constructors
    public Inventory() {}

    public Inventory(Integer inventoryItemId, String name, Integer quantity,
                     LocalDate lastRestockDate, Integer reorderLevel) {
        this.inventoryItemId = inventoryItemId;
        this.name = name;
        this.quantity = quantity != null ? quantity : 0;
        this.lastRestockDate = lastRestockDate;
        this.reorderLevel = reorderLevel != null ? reorderLevel : 0;
    }

    // Getters and Setters
    public Integer getInventoryItemId() { return inventoryItemId; }
    public void setInventoryItemId(Integer inventoryItemId) { this.inventoryItemId = inventoryItemId; }

    public String getName() { return name != null ? name : ""; }
    public void setName(String name) { this.name = name; }

    public Integer getQuantity() { return quantity != null ? quantity : 0; }
    public void setQuantity(Integer quantity) { this.quantity = quantity != null ? quantity : 0; }

    public LocalDate getLastRestockDate() { return lastRestockDate; }
    public void setLastRestockDate(LocalDate lastRestockDate) { this.lastRestockDate = lastRestockDate; }

    public Integer getReorderLevel() { return reorderLevel != null ? reorderLevel : 0; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel != null ? reorderLevel : 0; }

    // Helper method to check if item needs restocking
    public boolean needsRestocking() {
        return quantity <= reorderLevel;
    }

    // Helper method to get status
    public String getStatus() {
        if (quantity == 0) {
            return "Out of Stock";
        } else if (needsRestocking()) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }
}