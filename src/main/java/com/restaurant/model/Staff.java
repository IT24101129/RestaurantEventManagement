package com.restaurant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "staff")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Column(name = "phone", nullable = false)
    private String phone;

    @NotNull(message = "Position is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false)
    private Position position;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "max_hours_per_week")
    private Integer maxHoursPerWeek = 40;

    @Column(name = "preferred_shift_start")
    private String preferredShiftStart; // Format: "HH:mm"

    @Column(name = "preferred_shift_end")
    private String preferredShiftEnd; // Format: "HH:mm"

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Schedule> schedules;

    // Constructors
    public Staff() {}

    public Staff(String name, String email, String phone, Position position) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.position = position;
        this.isAvailable = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { 
        this.name = name; 
        this.updatedAt = LocalDateTime.now();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { 
        this.email = email; 
        this.updatedAt = LocalDateTime.now();
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { 
        this.phone = phone; 
        this.updatedAt = LocalDateTime.now();
    }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { 
        this.position = position; 
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { 
        this.isAvailable = isAvailable; 
        this.updatedAt = LocalDateTime.now();
    }

    public Integer getMaxHoursPerWeek() { return maxHoursPerWeek; }
    public void setMaxHoursPerWeek(Integer maxHoursPerWeek) { 
        this.maxHoursPerWeek = maxHoursPerWeek; 
        this.updatedAt = LocalDateTime.now();
    }

    public String getPreferredShiftStart() { return preferredShiftStart; }
    public void setPreferredShiftStart(String preferredShiftStart) { 
        this.preferredShiftStart = preferredShiftStart; 
        this.updatedAt = LocalDateTime.now();
    }

    public String getPreferredShiftEnd() { return preferredShiftEnd; }
    public void setPreferredShiftEnd(String preferredShiftEnd) { 
        this.preferredShiftEnd = preferredShiftEnd; 
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Schedule> getSchedules() { return schedules; }
    public void setSchedules(List<Schedule> schedules) { this.schedules = schedules; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Position {
        WAITER,
        CHEF,
        BARTENDER,
        HOST,
        MANAGER,
        CLEANER,
        CASHIER,
        KITCHEN_ASSISTANT,
        KITCHEN_STAFF
    }
}
