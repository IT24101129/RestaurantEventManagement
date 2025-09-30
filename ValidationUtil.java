package com.resturant.restaurantapp.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    // Username validation patterns
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{3,20}$";
    private static final Pattern usernamePattern = Pattern.compile(USERNAME_PATTERN);
    
    // Password validation patterns
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private static final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
    
    // Email validation pattern
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    
    // Phone number validation patterns (Sri Lankan format)
    private static final String PHONE_PATTERN = "^(\\+94|0)?[0-9]{9}$";
    private static final Pattern phonePattern = Pattern.compile(PHONE_PATTERN);
    
    /**
     * Validates username
     * Rules: 3-20 characters, alphanumeric and underscore only
     */
    public static ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new ValidationResult(false, "Username is required");
        }
        
        if (username.length() < 3) {
            return new ValidationResult(false, "Username must be at least 3 characters long");
        }
        
        if (username.length() > 20) {
            return new ValidationResult(false, "Username must not exceed 20 characters");
        }
        
        if (!usernamePattern.matcher(username).matches()) {
            return new ValidationResult(false, "Username can only contain letters, numbers, and underscores");
        }
        
        return new ValidationResult(true, "Valid username");
    }
    
    /**
     * Validates password
     * Rules: At least 8 characters, must contain uppercase, lowercase, digit, and special character
     */
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return new ValidationResult(false, "Password is required");
        }
        
        if (password.length() < 8) {
            return new ValidationResult(false, "Password must be at least 8 characters long");
        }
        
        if (!passwordPattern.matcher(password).matches()) {
            return new ValidationResult(false, 
                "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&)");
        }
        
        return new ValidationResult(true, "Valid password");
    }
    
    /**
     * Validates email address
     */
    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "Email is required");
        }
        
        if (!emailPattern.matcher(email).matches()) {
            return new ValidationResult(false, "Please enter a valid email address");
        }
        
        return new ValidationResult(true, "Valid email");
    }
    
    /**
     * Validates phone number (Sri Lankan format)
     * Rules: 9 digits, optional +94 or 0 prefix
     */
    public static ValidationResult validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return new ValidationResult(false, "Phone number is required");
        }
        
        // Remove spaces and dashes
        String cleanPhone = phoneNumber.replaceAll("[\\s-]", "");
        
        if (!phonePattern.matcher(cleanPhone).matches()) {
            return new ValidationResult(false, "Please enter a valid Sri Lankan phone number (e.g., 0771234567 or +94771234567)");
        }
        
        return new ValidationResult(true, "Valid phone number");
    }
    
    /**
     * Validates full name
     * Rules: 2-50 characters, letters and spaces only
     */
    public static ValidationResult validateFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return new ValidationResult(false, "Full name is required");
        }
        
        if (fullName.trim().length() < 2) {
            return new ValidationResult(false, "Full name must be at least 2 characters long");
        }
        
        if (fullName.trim().length() > 50) {
            return new ValidationResult(false, "Full name must not exceed 50 characters");
        }
        
        if (!fullName.matches("^[a-zA-Z\\s]+$")) {
            return new ValidationResult(false, "Full name can only contain letters and spaces");
        }
        
        return new ValidationResult(true, "Valid full name");
    }
    
    /**
     * Validation result class
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
