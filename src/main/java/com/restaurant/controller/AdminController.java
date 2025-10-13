package com.restaurant.controller;

import com.restaurant.model.User;
import com.restaurant.model.Staff;
import com.restaurant.service.userService;
import com.restaurant.service.AdminService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class AdminController {

    private final userService userService;
    private final AdminService adminService;

    public AdminController(userService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Optional<User> user = userService.findByEmail(username);
            if (user.isPresent()) {
                model.addAttribute("user", user.get());
            }

            // Load dashboard statistics
            Map<String, Object> stats = adminService.getDashboardStats();
            model.addAttribute("stats", stats);

            // Load recent activity
            List<Map<String, Object>> recentActivity = adminService.getRecentActivity();
            model.addAttribute("recentActivity", recentActivity);

            // Load system health
            Map<String, Object> systemHealth = adminService.getSystemHealth();
            model.addAttribute("systemHealth", systemHealth);

            return "admin/dashboard";

        } catch (Exception e) {
            model.addAttribute("error", "Failed to load admin dashboard: " + e.getMessage());
            return "admin/dashboard";
        }
    }

    @GetMapping("/admin/menu")
    public String adminMenu(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Optional<User> user = userService.findByEmail(username);
            if (user.isPresent()) {
                model.addAttribute("user", user.get());
            }

            List<com.restaurant.model.MenuCategory> categories = adminService.getActiveCategoriesOrdered();
            model.addAttribute("categories", categories);

            List<com.restaurant.model.MenuItem> items = adminService.getAllMenuItems();
            model.addAttribute("menuItems", items);

            return "admin/menu";

        } catch (Exception e) {
            model.addAttribute("error", "Failed to load menu: " + e.getMessage());
            model.addAttribute("categories", java.util.List.of());
            model.addAttribute("menuItems", java.util.List.of());
            return "admin/menu";
        }
    }

    @GetMapping("/admin/tables")
    public String adminTables(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Optional<User> user = userService.findByEmail(username);
            if (user.isPresent()) {
                model.addAttribute("user", user.get());
            }

            List<com.restaurant.model.RestaurantTable> tables = adminService.getAllTables();
            model.addAttribute("tables", tables);

            return "admin/tables";

        } catch (Exception e) {
            model.addAttribute("error", "Failed to load tables: " + e.getMessage());
            model.addAttribute("tables", java.util.List.of());
            return "admin/tables";
        }
    }

    @GetMapping("/admin/restaurants")
    public String adminRestaurants(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Optional<User> user = userService.findByEmail(username);
            if (user.isPresent()) {
                model.addAttribute("user", user.get());
            }

            List<com.restaurant.model.RestaurantTable> tables = adminService.getAllTables();
            model.addAttribute("tables", tables);

            return "admin/restaurants";

        } catch (Exception e) {
            model.addAttribute("error", "Failed to load restaurants: " + e.getMessage());
            model.addAttribute("tables", java.util.List.of());
            return "admin/restaurants";
        }
    }

    @GetMapping("/admin/users")
    public String adminUsers(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Optional<User> user = userService.findByEmail(username);
            if (user.isPresent()) {
                model.addAttribute("user", user.get());
            }

            // Load all users
            List<User> allUsers = adminService.getAllUsers();
            model.addAttribute("allUsers", allUsers);

            // Load users by role
            Map<User.Role, List<User>> usersByRole = allUsers.stream()
                .collect(java.util.stream.Collectors.groupingBy(User::getRole));
            model.addAttribute("usersByRole", usersByRole);

            // Load staff
            List<Staff> allStaff = adminService.getAllStaff();
            model.addAttribute("allStaff", allStaff);

            return "admin/users";

        } catch (Exception e) {
            model.addAttribute("error", "Failed to load users: " + e.getMessage());
            return "admin/users";
        }
    }

    @GetMapping("/admin/settings")
    public String adminSettings(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Optional<User> user = userService.findByEmail(username);
            if (user.isPresent()) {
                model.addAttribute("user", user.get());
            }

            // Load system health for settings
            Map<String, Object> systemHealth = adminService.getSystemHealth();
            model.addAttribute("systemHealth", systemHealth);

            return "admin/settings";

        } catch (Exception e) {
            model.addAttribute("error", "Failed to load settings: " + e.getMessage());
            return "admin/settings";
        }
    }

    @GetMapping("/admin/reports")
    public String adminReports(Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Optional<User> user = userService.findByEmail(username);
            if (user.isPresent()) {
                model.addAttribute("user", user.get());
            }

            // Load system report
            Map<String, Object> systemReport = adminService.getSystemReport();
            model.addAttribute("systemReport", systemReport);

            // Load dashboard stats for reports
            Map<String, Object> stats = adminService.getDashboardStats();
            model.addAttribute("stats", stats);

            return "admin/reports";

        } catch (Exception e) {
            model.addAttribute("error", "Failed to load reports: " + e.getMessage());
            return "admin/reports";
        }
    }

    // API endpoints for AJAX calls
    @GetMapping("/admin/api/dashboard-stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDashboardStatsApi() {
        try {
            Map<String, Object> stats = adminService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", "Failed to load dashboard stats: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/admin/api/system-health")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSystemHealthApi() {
        try {
            Map<String, Object> health = adminService.getSystemHealth();
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", "Failed to load system health: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/admin/api/users/{userId}/role")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateUserRole(@PathVariable Long userId, @RequestParam String role) {
        try {
            User.Role newRole = User.Role.valueOf(role.toUpperCase());
            User updatedUser = adminService.updateUserRole(userId, newRole);
            
            if (updatedUser != null) {
                Map<String, Object> response = Map.of(
                    "success", true,
                    "message", "User role updated successfully",
                    "user", updatedUser
                );
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = Map.of("success", false, "error", "User not found");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            Map<String, Object> error = Map.of("success", false, "error", "Failed to update user role: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/admin/api/users/{userId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long userId) {
        try {
            boolean deleted = adminService.deleteUser(userId);
            if (deleted) {
                Map<String, Object> response = Map.of("success", true, "message", "User deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = Map.of("success", false, "error", "Failed to delete user");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            Map<String, Object> error = Map.of("success", false, "error", "Failed to delete user: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
