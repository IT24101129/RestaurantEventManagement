package com.resturant.restaurantapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "event_menu_selections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventMenuSelection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "selection_id", unique = true, nullable = false)
    private String selectionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu selectedMenu;
    
    @Column(name = "number_of_guests", nullable = false)
    private Integer numberOfGuests;
    
    @Column(name = "special_notes", columnDefinition = "TEXT")
    private String specialNotes;
    
    @Column(name = "dietary_restrictions", columnDefinition = "TEXT")
    private String dietaryRestrictions;
    
    @Column(name = "custom_changes", columnDefinition = "TEXT")
    private String customChanges;
    
    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "selection_status", nullable = false)
    private SelectionStatus selectionStatus = SelectionStatus.PENDING;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (selectionId == null) {
            selectionId = generateUniqueSelectionId();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generateUniqueSelectionId() {
        String prefix = "MENU_SEL";
        long timestamp = System.currentTimeMillis();
        return prefix + timestamp;
    }
    
    public enum SelectionStatus {
        PENDING("Pending Approval"),
        CONFIRMED("Menu Confirmed"),
        APPROVED("Approved by Chef"),
        IN_PREPARATION("In Preparation"),
        READY("Ready for Service"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled");
        
        private final String displayName;
        
        SelectionStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
