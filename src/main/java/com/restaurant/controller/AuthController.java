package com.restaurant.controller;

import com.restaurant.model.User;
import com.restaurant.service.userService; // Assuming your service is named like this
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final userService userService;

    public AuthController(userService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "index"; // Return the view name if using templates, or redirect to static if you prefer
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";  // Return view name for login page (Thymeleaf or JSP)
        // If you serve only static login.html, use: return "redirect:/login.html";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";  // Return view name for registration form
        // Or "redirect:/register.html" if using static files
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register"; // Back to registration view on error
        }

        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("message", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Email already exists or registration failed: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<User> user = userService.findByEmail(username);
        if (user.isPresent()) {
            User u = user.get();
            model.addAttribute("user", u);
            
            // Role-based dashboard routing
            switch (u.getRole()) {
                case RESTAURANT_MANAGER:
                    return "redirect:/manager/dashboard";
                case HEAD_CHEF:
                    return "redirect:/chef/dashboard";
                case BANQUET_HALL_SUPERVISOR:
                    return "redirect:/banquet/dashboard";
                case CUSTOMER_RELATIONS_OFFICER:
                    return "redirect:/customer-relations/dashboard";
                case ADMIN:
                    return "redirect:/admin/dashboard";
                case CUSTOMER:
                default:
                    return "redirect:/reservations"; // Customer goes to reservations page
            }
        }

        // Fallback for unauthenticated users
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<User> user = userService.findByEmail(username);
        if (user.isPresent()) {
            User u = user.get();
            model.addAttribute("user", u);
        }

        return "home";
    }
}
