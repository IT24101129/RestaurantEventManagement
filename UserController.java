package com.resturant.restaurantapp.controller;

import com.resturant.restaurantapp.model.User;
import com.resturant.restaurantapp.service.UserService;
import com.resturant.restaurantapp.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/users")
    public String userManagementPage(Model model) {
        List<User> allUsers = userService.getAllUsers();
        model.addAttribute("users", allUsers);
        model.addAttribute("userTypes", User.UserType.values());
        model.addAttribute("roles", User.Role.values());
        return "user-management";
    }
    
    @GetMapping("/users/add")
    public String addUserPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("userTypes", User.UserType.values());
        model.addAttribute("roles", User.Role.values());
        return "add-user";
    }
    
    @PostMapping("/users/add")
    public String addUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            // Validate input fields
            ValidationUtil.ValidationResult fullNameResult = ValidationUtil.validateFullName(user.getFullName());
            if (!fullNameResult.isValid()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Full Name: " + fullNameResult.getMessage());
                return "redirect:/admin/users/add";
            }
            
            ValidationUtil.ValidationResult usernameResult = ValidationUtil.validateUsername(user.getUsername());
            if (!usernameResult.isValid()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Username: " + usernameResult.getMessage());
                return "redirect:/admin/users/add";
            }
            
            ValidationUtil.ValidationResult emailResult = ValidationUtil.validateEmail(user.getEmail());
            if (!emailResult.isValid()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email: " + emailResult.getMessage());
                return "redirect:/admin/users/add";
            }
            
            // Phone number is optional for admin users, but validate if provided
            if (user.getContactNumber() != null && !user.getContactNumber().trim().isEmpty()) {
                ValidationUtil.ValidationResult phoneResult = ValidationUtil.validatePhoneNumber(user.getContactNumber());
                if (!phoneResult.isValid()) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Phone Number: " + phoneResult.getMessage());
                    return "redirect:/admin/users/add";
                }
            }
            
            ValidationUtil.ValidationResult passwordResult = ValidationUtil.validatePassword(user.getPassword());
            if (!passwordResult.isValid()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Password: " + passwordResult.getMessage());
                return "redirect:/admin/users/add";
            }
            
            User savedUser = userService.createUser(user);
            redirectAttributes.addFlashAttribute("successMessage", 
                "User added successfully! User ID: " + savedUser.getUserId());
            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error adding user: " + e.getMessage());
            return "redirect:/admin/users/add";
        }
    }
    
    @GetMapping("/users/edit/{id}")
    public String editUserPage(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("userTypes", User.UserType.values());
        model.addAttribute("roles", User.Role.values());
        return "edit-user";
    }
    
    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, 
                           RedirectAttributes redirectAttributes) {
        try {
            // Validate input fields
            ValidationUtil.ValidationResult fullNameResult = ValidationUtil.validateFullName(user.getFullName());
            if (!fullNameResult.isValid()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Full Name: " + fullNameResult.getMessage());
                return "redirect:/admin/users/edit/" + id;
            }
            
            ValidationUtil.ValidationResult usernameResult = ValidationUtil.validateUsername(user.getUsername());
            if (!usernameResult.isValid()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Username: " + usernameResult.getMessage());
                return "redirect:/admin/users/edit/" + id;
            }
            
            ValidationUtil.ValidationResult emailResult = ValidationUtil.validateEmail(user.getEmail());
            if (!emailResult.isValid()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email: " + emailResult.getMessage());
                return "redirect:/admin/users/edit/" + id;
            }
            
            // Phone number is optional for admin users, but validate if provided
            if (user.getContactNumber() != null && !user.getContactNumber().trim().isEmpty()) {
                ValidationUtil.ValidationResult phoneResult = ValidationUtil.validatePhoneNumber(user.getContactNumber());
                if (!phoneResult.isValid()) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Phone Number: " + phoneResult.getMessage());
                    return "redirect:/admin/users/edit/" + id;
                }
            }
            
            user.setId(id);
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("successMessage", 
                "User updated successfully!");
            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error updating user: " + e.getMessage());
            return "redirect:/admin/users/edit/" + id;
        }
    }
    
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/deactivate/{id}")
    public String deactivateUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deactivateUser(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "User deactivated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error deactivating user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/activate/{id}")
    public String activateUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.activateUser(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                "User activated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error activating user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    @GetMapping("/users/by-type/{userType}")
    public String getUsersByType(@PathVariable String userType, Model model) {
        try {
            User.UserType type = User.UserType.valueOf(userType.toUpperCase());
            List<User> users = userService.getUsersByType(type);
            model.addAttribute("users", users);
            model.addAttribute("userTypes", User.UserType.values());
            model.addAttribute("roles", User.Role.values());
            model.addAttribute("selectedType", type);
            return "user-management";
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/users";
        }
    }
}

