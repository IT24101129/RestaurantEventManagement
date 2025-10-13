package com.restaurant.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_equipment")
public class EventEquipment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_booking_id", nullable = false)
    private EventBooking eventBooking;
    
    @Column(name = "equipment_name", nullable = false)
    private String equipmentName;
    
    @Column(name = "equipment_type", nullable = false)
    private String equipmentType; // e.g., "Audio", "Visual", "Furniture", "Catering"
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitCost;
    
    @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private EquipmentStatus status = EquipmentStatus.REQUESTED;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "assigned_by")
    private String assignedBy;
    
    // Constructors
    public EventEquipment() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public EventEquipment(EventBooking eventBooking, String equipmentName, String equipmentType,
                         Integer quantity, BigDecimal unitCost, String assignedBy) {
        this();
        this.eventBooking = eventBooking;
        this.equipmentName = equipmentName;
        this.equipmentType = equipmentType;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.totalCost = unitCost.multiply(BigDecimal.valueOf(quantity));
        this.assignedBy = assignedBy;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public EventBooking getEventBooking() { return eventBooking; }
    public void setEventBooking(EventBooking eventBooking) { this.eventBooking = eventBooking; }
    
    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
    
    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity;
        if (unitCost != null) {
            this.totalCost = unitCost.multiply(BigDecimal.valueOf(quantity));
        }
    }
    
    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { 
        this.unitCost = unitCost;
        this.totalCost = unitCost.multiply(BigDecimal.valueOf(quantity));
    }
    
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    
    public EquipmentStatus getStatus() { return status; }
    public void setStatus(EquipmentStatus status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Equipment Status Enum
    public enum EquipmentStatus {
        REQUESTED("Requested"),
        CONFIRMED("Confirmed"),
        SETUP("Setup"),
        IN_USE("In Use"),
        CLEANUP("Cleanup"),
        RETURNED("Returned"),
        DAMAGED("Damaged");
        
        private final String displayName;
        
        EquipmentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
