package com.resturant.restaurantapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "event_id", unique = true, nullable = false)
    private String eventId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;
    
    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;
    
    @Column(name = "event_time", nullable = false)
    private LocalTime eventTime;
    
    @Column(name = "customer_name", nullable = false)
    private String customerName;
    
    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;
    
    @Column(name = "customer_email")
    private String customerEmail;
    
    @Column(name = "number_of_guests", nullable = false)
    private Integer numberOfGuests;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_package", nullable = false)
    private EventPackage eventPackage;
    
    @Column(name = "special_notes", columnDefinition = "TEXT")
    private String specialNotes;
    
    @Column(name = "assigned_staff")
    private String assignedStaff;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false)
    private BookingStatus bookingStatus = BookingStatus.PENDING;
    
    @Column(name = "total_cost")
    private Double totalCost;
    
    @Column(name = "advance_payment")
    private Double advancePayment;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (eventId == null) {
            eventId = generateUniqueEventId();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // Don't modify createdAt - preserve the original creation time
    }
    
    private String generateUniqueEventId() {
        String prefix = "EVT";
        long timestamp = System.currentTimeMillis();
        return prefix + timestamp;
    }
    
    public enum EventType {
        WEDDING("Wedding"),
        BIRTHDAY("Birthday Party"),
        CORPORATE("Corporate Event"),
        ANNIVERSARY("Anniversary"),
        GRADUATION("Graduation Party"),
        OTHER("Other");
        
        private final String displayName;
        
        EventType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum EventPackage {
        BASIC("Basic Package - Food Only"),
        STANDARD("Standard Package - Food + Basic Decoration"),
        PREMIUM("Premium Package - Food + Full Decoration + Music"),
        DELUXE("Deluxe Package - Food + Full Decoration + Music + Photography"),
        CUSTOM("Custom Package");
        
        private final String description;
        
        EventPackage(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public enum BookingStatus {
        PENDING("Pending Approval"),
        CONFIRMED("Confirmed"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled");
        
        private final String displayName;
        
        BookingStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}