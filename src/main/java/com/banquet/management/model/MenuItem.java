package com.banquet.management.model;

import java.math.BigDecimal;

public class MenuItem {
    private Integer itemId;
    private String name;
    private String ingredients;
    private String description;
    private BigDecimal price;
    private String availability;

    // Constructors
    public MenuItem() {}

    public MenuItem(Integer itemId, String name, String ingredients, String description,
                    BigDecimal price, String availability) {
        this.itemId = itemId;
        this.name = name != null ? name : "";
        this.ingredients = ingredients != null ? ingredients : "";
        this.description = description != null ? description : "";
        this.price = price != null ? price : BigDecimal.ZERO;
        this.availability = availability != null ? availability : "Available";
    }

    // Getters and Setters
    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }

    public String getName() { return name != null ? name : ""; }
    public void setName(String name) { this.name = name; }

    public String getIngredients() { return ingredients != null ? ingredients : ""; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getDescription() { return description != null ? description : ""; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price != null ? price : BigDecimal.ZERO; }
    public void setPrice(BigDecimal price) { this.price = price != null ? price : BigDecimal.ZERO; }

    public String getAvailability() { return availability != null ? availability : "Available"; }
    public void setAvailability(String availability) { this.availability = availability != null ? availability : "Available"; }

    // Helper method to check if item is available
    public boolean isAvailable() {
        return "Available".equalsIgnoreCase(availability);
    }
}