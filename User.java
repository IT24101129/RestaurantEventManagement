package com.resturant.restaurantapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;
    
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    
    @Column(name = "contact_number")
    private String contactNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (userId == null) {
            userId = generateUniqueUserId();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generateUniqueUserId() {
        String prefix = userType == UserType.STAFF ? "STF" : "CUS";
        long timestamp = System.currentTimeMillis();
        return prefix + timestamp;
    }
    
    public enum UserType {
        STAFF, CUSTOMER
    }
    
    public enum Role {
        ADMIN, CHEF, COORDINATOR, CUSTOMER_RELATIONS_OFFICER, BANQUET_HALL_SUPERVISOR, MANAGER, CUSTOMER
    }
}

