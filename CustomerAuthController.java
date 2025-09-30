package com.resturant.restaurantapp.controller;

import com.resturant.restaurantapp.model.User;
import com.resturant.restaurantapp.service.UserService;
import com.resturant.restaurantapp.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/customer")
public class CustomerAuthController {
    
    @Autowired
    private UserService userService;
    
    // Customer Registration Page
    @GetMapping("/register")
    public String customerRegisterPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("userTypes", User.UserType.values());
        model.addAttribute("roles", User.Role.values());
        return "customer-register";
    }
    
    @PostMapping("/register")
    public String registerCustomer(@ModelAttribute User user, 
                                  RedirectAttributes redirectAttributes) {
        try {
            // Validate input fields
            ValidationUtil.ValidationResult fullNameResult = ValidationUtil.validateFullName(user.getFullName());
            if (!fullNameResult.isValid()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Full Name: " + fullNameResult.getMessage());
                return "redirect:/customer/register";
            }
            
            ValidationUtil.ValidationResult usernameResult = ValidationUtil.validateUsername(user.getUsername());
            if (!usernameResult.isValid()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Username: " + usernameResult.getMessage());
                return "redirect:/customer/register";
            }
            
            ValidationUtil.ValidationResult emailResult = ValidationUtil.validateEmail(user.getEmail());
            if (!emailResult.isValid()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email: " + emailResult.getMessage());
                return "redirect:/customer/register";
            }
            
            ValidationUtil.ValidationResult phoneResult = ValidationUtil.validatePhoneNumber(user.getContactNumber());
            if (!phoneResult.isValid()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Phone Number: " + phoneResult.getMessage());
                return "redirect:/customer/register";
            }
            
            ValidationUtil.ValidationResult passwordResult = ValidationUtil.validatePassword(user.getPassword());
            if (!passwordResult.isValid()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Password: " + passwordResult.getMessage());
                return "redirect:/customer/register";
            }
            
            // Set customer-specific defaults
            user.setUserType(User.UserType.CUSTOMER);
            user.setRole(User.Role.CUSTOMER);
            user.setIsActive(true);
            
            User savedUser = userService.createUser(user);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Registration successful! Please login with your credentials.");
            return "redirect:/customer/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Registration failed: " + e.getMessage());
            return "redirect:/customer/register";
        }
    }
    
    // Customer Login Page
    @GetMapping("/login")
    public String customerLoginPage() {
        return "customer-login";
    }
    
    @PostMapping("/login")
    public String customerLogin(@RequestParam String username, 
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            boolean isValid = userService.validateLogin(username, password);
            if (isValid) {
                // Get user details
                Optional<User> userOpt = userService.getUserByUsername(username);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    
                    // Check if user is a customer
                    if (user.getUserType() == User.UserType.CUSTOMER) {
                        // Set session attributes
                        session.setAttribute("customerId", user.getId());
                        session.setAttribute("customerName", user.getFullName());
                        session.setAttribute("customerUsername", user.getUsername());
                        session.setAttribute("customerEmail", user.getEmail());
                        session.setAttribute("customerPhone", user.getContactNumber());
                        
                        redirectAttributes.addFlashAttribute("successMessage", 
                            "Welcome back, " + user.getFullName() + "!");
                        return "redirect:/customer/dashboard";
                    } else {
                        redirectAttributes.addFlashAttribute("errorMessage", 
                            "This account is not for customers. Please use the admin login.");
                        return "redirect:/customer/login";
                    }
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", 
                        "User not found!");
                    return "redirect:/customer/login";
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Invalid username or password!");
                return "redirect:/customer/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Login failed: " + e.getMessage());
            return "redirect:/customer/login";
        }
    }
    
    // Customer Dashboard
    @GetMapping("/dashboard")
    public String customerDashboard(HttpSession session, Model model) {
        // Check if customer is logged in
        if (session.getAttribute("customerId") == null) {
            return "redirect:/customer/login";
        }
        
        model.addAttribute("customerName", session.getAttribute("customerName"));
        model.addAttribute("customerEmail", session.getAttribute("customerEmail"));
        model.addAttribute("customerPhone", session.getAttribute("customerPhone"));
        
        return "customer-dashboard";
    }
    
    // Customer Profile Page
    @GetMapping("/profile")
    public String customerProfile(HttpSession session, Model model) {
        if (session.getAttribute("customerId") == null) {
            return "redirect:/customer/login";
        }
        
        Long customerId = (Long) session.getAttribute("customerId");
        Optional<User> userOpt = userService.getUserById(customerId);
        
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            return "customer-profile";
        } else {
            return "redirect:/customer/login";
        }
    }
    
    @PostMapping("/profile")
    public String updateCustomerProfile(@ModelAttribute User user,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        try {
            if (session.getAttribute("customerId") == null) {
                return "redirect:/customer/login";
            }
            
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Profile updated successfully!");
            
            // Update session attributes
            session.setAttribute("customerName", user.getFullName());
            session.setAttribute("customerEmail", user.getEmail());
            session.setAttribute("customerPhone", user.getContactNumber());
            
            return "redirect:/customer/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Profile update failed: " + e.getMessage());
            return "redirect:/customer/profile";
        }
    }
    
    // Customer Logout
    @GetMapping("/logout")
    public String customerLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", 
            "You have been logged out successfully!");
        return "redirect:/customer/login";
    }
}
