package com.restaurant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;

    @Column(name = "last_points_update")
    private LocalDateTime lastPointsUpdate;

    public User() {}

    public User(String name, String email, String password, String phone, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; this.updatedAt = LocalDateTime.now(); }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; this.updatedAt = LocalDateTime.now(); }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; this.updatedAt = LocalDateTime.now(); }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; this.updatedAt = LocalDateTime.now(); }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; this.updatedAt = LocalDateTime.now(); }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(Integer loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public LocalDateTime getLastPointsUpdate() { return lastPointsUpdate; }
    public void setLastPointsUpdate(LocalDateTime lastPointsUpdate) { this.lastPointsUpdate = lastPointsUpdate; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Role {
        CUSTOMER,
        RESTAURANT_MANAGER,
        EVENT_COORDINATOR,
        HEAD_CHEF,
        CUSTOMER_RELATIONS_OFFICER,
        BANQUET_HALL_SUPERVISOR,
        STAFF, ADMIN
    }
}
