package com.resturant.restaurantapp.controller;

import com.resturant.restaurantapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password,
                       RedirectAttributes redirectAttributes) {
        try {
            boolean isValid = userService.validateLogin(username, password);
            if (isValid) {
                redirectAttributes.addFlashAttribute("successMessage", "Login successful!");
                return "redirect:/admin";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid username or password!");
                return "redirect:/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Login failed: " + e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/login-success")
    public String loginSuccessPage() {
        return "admin-dashboard";
    }
    
    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin-dashboard";
    }
}
