package com.resturant.restaurantapp.service;

import com.resturant.restaurantapp.model.User;
import com.resturant.restaurantapp.repository.UserRepository;
import com.resturant.restaurantapp.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User createUser(User user) {
        // Additional validation in service layer (controllers already validate format)
        ValidationUtil.ValidationResult fullNameResult = ValidationUtil.validateFullName(user.getFullName());
        if (!fullNameResult.isValid()) {
            throw new RuntimeException("Full Name: " + fullNameResult.getMessage());
        }
        
        ValidationUtil.ValidationResult usernameResult = ValidationUtil.validateUsername(user.getUsername());
        if (!usernameResult.isValid()) {
            throw new RuntimeException("Username: " + usernameResult.getMessage());
        }
        
        ValidationUtil.ValidationResult emailResult = ValidationUtil.validateEmail(user.getEmail());
        if (!emailResult.isValid()) {
            throw new RuntimeException("Email: " + emailResult.getMessage());
        }
        
        if (user.getContactNumber() != null && !user.getContactNumber().trim().isEmpty()) {
            ValidationUtil.ValidationResult phoneResult = ValidationUtil.validatePhoneNumber(user.getContactNumber());
            if (!phoneResult.isValid()) {
                throw new RuntimeException("Phone Number: " + phoneResult.getMessage());
            }
        }
        
        ValidationUtil.ValidationResult passwordResult = ValidationUtil.validatePassword(user.getPassword());
        if (!passwordResult.isValid()) {
            throw new RuntimeException("Password: " + passwordResult.getMessage());
        }
        
        // Validate unique constraints
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        
        return userRepository.save(user);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> getUsersByType(User.UserType userType) {
        return userRepository.findByUserType(userType);
    }
    
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> getActiveUsers() {
        return userRepository.findByIsActive(true);
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> getUserByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }
    
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new RuntimeException("User ID cannot be null for update");
        }
        
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + user.getId());
        }
        
        // Validate input fields
        ValidationUtil.ValidationResult fullNameResult = ValidationUtil.validateFullName(user.getFullName());
        if (!fullNameResult.isValid()) {
            throw new RuntimeException("Full Name: " + fullNameResult.getMessage());
        }
        
        ValidationUtil.ValidationResult usernameResult = ValidationUtil.validateUsername(user.getUsername());
        if (!usernameResult.isValid()) {
            throw new RuntimeException("Username: " + usernameResult.getMessage());
        }
        
        ValidationUtil.ValidationResult emailResult = ValidationUtil.validateEmail(user.getEmail());
        if (!emailResult.isValid()) {
            throw new RuntimeException("Email: " + emailResult.getMessage());
        }
        
        if (user.getContactNumber() != null && !user.getContactNumber().trim().isEmpty()) {
            ValidationUtil.ValidationResult phoneResult = ValidationUtil.validatePhoneNumber(user.getContactNumber());
            if (!phoneResult.isValid()) {
                throw new RuntimeException("Phone Number: " + phoneResult.getMessage());
            }
        }
        
        // Check for unique constraints (excluding current user)
        User existing = existingUser.get();
        if (!existing.getUsername().equals(user.getUsername()) && 
            userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }
        
        if (!existing.getEmail().equals(user.getEmail()) && 
            userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        
        userRepository.deleteById(id);
    }
    
    public void deactivateUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        
        User user = userOpt.get();
        user.setIsActive(false);
        userRepository.save(user);
    }
    
    public void activateUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        
        User user = userOpt.get();
        user.setIsActive(true);
        userRepository.save(user);
    }
    
    public boolean validateLogin(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        return user.getPassword().equals(password) && user.getIsActive();
    }
}

