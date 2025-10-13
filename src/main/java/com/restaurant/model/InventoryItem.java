package com.restaurant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "item_name", nullable = false, unique = true)
    private String itemName;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;
    
    @Column(name = "current_stock", nullable = false)
    private Double currentStock;
    
    @Column(name = "minimum_stock", nullable = false)
    private Double minimumStock;
    
    @Column(name = "maximum_stock", nullable = false)
    private Double maximumStock;
    
    @Column(name = "unit", nullable = false)
    private String unit;
    
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;
    
    @Column(name = "supplier", nullable = false)
    private String supplier;
    
    @Column(name = "is_low_stock", nullable = false)
    private Boolean isLowStock = false;
    
    @Column(name = "last_restocked")
    private LocalDateTime lastRestocked;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public InventoryItem() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public InventoryItem(String itemName, Category category, Double currentStock, 
                        Double minimumStock, Double maximumStock, String unit, 
                        Double unitPrice, String supplier) {
        this();
        this.itemName = itemName;
        this.category = category;
        this.currentStock = currentStock;
        this.minimumStock = minimumStock;
        this.maximumStock = maximumStock;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.supplier = supplier;
        this.isLowStock = currentStock <= minimumStock;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public Double getCurrentStock() {
        return currentStock;
    }
    
    public void setCurrentStock(Double currentStock) {
        this.currentStock = currentStock;
        if (minimumStock != null) {
            this.isLowStock = currentStock <= minimumStock;
        }
    }
    
    public Double getMinimumStock() {
        return minimumStock;
    }
    
    public void setMinimumStock(Double minimumStock) {
        this.minimumStock = minimumStock;
        if (currentStock != null) {
            this.isLowStock = currentStock <= minimumStock;
        }
    }
    
    public Double getMaximumStock() {
        return maximumStock;
    }
    
    public void setMaximumStock(Double maximumStock) {
        this.maximumStock = maximumStock;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public Double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public String getSupplier() {
        return supplier;
    }
    
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
    
    public Boolean getIsLowStock() {
        return isLowStock;
    }
    
    public void setIsLowStock(Boolean isLowStock) {
        this.isLowStock = isLowStock;
    }
    
    public LocalDateTime getLastRestocked() {
        return lastRestocked;
    }
    
    public void setLastRestocked(LocalDateTime lastRestocked) {
        this.lastRestocked = lastRestocked;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.isLowStock = currentStock <= minimumStock;
    }
    
    // Enums
    public enum Category {
        VEGETABLES("Vegetables"),
        MEAT("Meat & Poultry"),
        SEAFOOD("Seafood"),
        PROTEIN("Protein"),
        DAIRY("Dairy Products"),
        GRAINS("Grains & Cereals"),
        SPICES("Spices & Herbs"),
        BEVERAGES("Beverages"),
        DESSERT("Dessert"),
        VEGETABLE("Vegetable"),
        OTHER("Other");
        
        private final String displayName;
        
        Category(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
