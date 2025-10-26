package com.banquet.management.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private Integer orderId;
    private LocalDate orderDate;
    private String type;
    private String dietaryNotes;
    private BigDecimal totalCost;
    private String status;
    private Integer userId;
    private List<OrderMenuItem> orderItems;

    // Constructors
    public Order() {
        this.orderItems = new ArrayList<>();
        this.totalCost = BigDecimal.ZERO;
        this.status = "Pending";
    }

    public Order(Integer orderId, LocalDate orderDate, String type, String dietaryNotes,
                 BigDecimal totalCost, String status, Integer userId) {
        this();
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.type = type;
        this.dietaryNotes = dietaryNotes;
        this.totalCost = totalCost != null ? totalCost : BigDecimal.ZERO;
        this.status = status != null ? status : "Pending";
        this.userId = userId;
    }

    // Getters and Setters with null handling
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public String getType() { return type != null ? type : ""; }
    public void setType(String type) { this.type = type; }

    public String getDietaryNotes() { return dietaryNotes != null ? dietaryNotes : ""; }
    public void setDietaryNotes(String dietaryNotes) { this.dietaryNotes = dietaryNotes; }

    public BigDecimal getTotalCost() { return totalCost != null ? totalCost : BigDecimal.ZERO; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost != null ? totalCost : BigDecimal.ZERO; }

    public String getStatus() { return status != null ? status : "Pending"; }
    public void setStatus(String status) { this.status = status != null ? status : "Pending"; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public List<OrderMenuItem> getOrderItems() {
        return orderItems != null ? orderItems : new ArrayList<>();
    }
    public void setOrderItems(List<OrderMenuItem> orderItems) {
        this.orderItems = orderItems != null ? orderItems : new ArrayList<>();
    }
}