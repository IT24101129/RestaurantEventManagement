package com.restaurant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_staff_assignments")
public class EventStaffAssignment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_booking_id", nullable = false)
    private EventBooking eventBooking;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;
    
    @Column(name = "role", nullable = false)
    private String role; // e.g., "Server", "Bartender", "Setup", "Cleanup"
    
    @Column(name = "assigned_hours")
    private Integer assignedHours;
    
    @Column(name = "start_time")
    private String startTime; // Format: "HH:mm"
    
    @Column(name = "end_time")
    private String endTime; // Format: "HH:mm"
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AssignmentStatus status = AssignmentStatus.ASSIGNED;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "assigned_by")
    private String assignedBy;
    
    // Constructors
    public EventStaffAssignment() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public EventStaffAssignment(EventBooking eventBooking, Staff staff, String role, 
                               Integer assignedHours, String startTime, String endTime, String assignedBy) {
        this();
        this.eventBooking = eventBooking;
        this.staff = staff;
        this.role = role;
        this.assignedHours = assignedHours;
        this.startTime = startTime;
        this.endTime = endTime;
        this.assignedBy = assignedBy;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public EventBooking getEventBooking() { return eventBooking; }
    public void setEventBooking(EventBooking eventBooking) { this.eventBooking = eventBooking; }
    
    public Staff getStaff() { return staff; }
    public void setStaff(Staff staff) { this.staff = staff; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Integer getAssignedHours() { return assignedHours; }
    public void setAssignedHours(Integer assignedHours) { this.assignedHours = assignedHours; }
    
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    
    public AssignmentStatus getStatus() { return status; }
    public void setStatus(AssignmentStatus status) { this.status = status; }
    
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
    
    // Assignment Status Enum
    public enum AssignmentStatus {
        ASSIGNED("Assigned"),
        CONFIRMED("Confirmed"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled");
        
        private final String displayName;
        
        AssignmentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
