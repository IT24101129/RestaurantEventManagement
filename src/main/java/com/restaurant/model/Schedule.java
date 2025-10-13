package com.restaurant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    @NotNull(message = "Staff is required")
    private Staff staff;

    @NotNull(message = "Date is required")
    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ScheduleStatus status = ScheduleStatus.SCHEDULED;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "created_by")
    private String createdBy; // Manager who created the schedule

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Schedule() {}

    public Schedule(Staff staff, LocalDate scheduleDate, LocalTime startTime, LocalTime endTime) {
        this.staff = staff;
        this.scheduleDate = scheduleDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = ScheduleStatus.SCHEDULED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Staff getStaff() { return staff; }
    public void setStaff(Staff staff) { 
        this.staff = staff; 
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(LocalDate scheduleDate) { 
        this.scheduleDate = scheduleDate; 
        this.updatedAt = LocalDateTime.now();
    }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { 
        this.startTime = startTime; 
        this.updatedAt = LocalDateTime.now();
    }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { 
        this.endTime = endTime; 
        this.updatedAt = LocalDateTime.now();
    }

    public ScheduleStatus getStatus() { return status; }
    public void setStatus(ScheduleStatus status) { 
        this.status = status; 
        this.updatedAt = LocalDateTime.now();
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { 
        this.notes = notes; 
        this.updatedAt = LocalDateTime.now();
    }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { 
        this.createdBy = createdBy; 
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public boolean hasConflictWith(Schedule other) {
        if (!this.scheduleDate.equals(other.scheduleDate)) {
            return false;
        }
        
        if (!this.staff.getId().equals(other.staff.getId())) {
            return false;
        }
        
        // Check for time overlap
        return !(this.endTime.isBefore(other.startTime) || this.startTime.isAfter(other.endTime));
    }

    public long getDurationInHours() {
        return java.time.Duration.between(startTime, endTime).toHours();
    }

    public enum ScheduleStatus {
        SCHEDULED,
        CONFIRMED,
        CANCELLED,
        COMPLETED,
        NO_SHOW
    }
}
