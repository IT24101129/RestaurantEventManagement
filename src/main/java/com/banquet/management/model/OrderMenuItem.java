package com.banquet.management.model;

public class OrderMenuItem {
    private Integer orderId;
    private Integer itemId;
    private Integer quantity;
    private String itemName;
    private String description;

    // Constructors, Getters and Setters
    public OrderMenuItem() {}

    public OrderMenuItem(Integer orderId, Integer itemId, Integer quantity) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}