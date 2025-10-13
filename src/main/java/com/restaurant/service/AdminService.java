package com.restaurant.service;

import com.restaurant.model.User;
import com.restaurant.model.Staff;
import com.restaurant.model.Order;
import com.restaurant.model.InventoryItem;
import com.restaurant.model.MenuItem;
import com.restaurant.model.MenuCategory;
import com.restaurant.model.RestaurantTable;
import com.restaurant.repository.UserRepository;
import com.restaurant.repository.StaffRepository;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.InventoryItemRepository;
import com.restaurant.repository.MenuItemRepository;
import com.restaurant.repository.MenuCategoryRepository;
import com.restaurant.repository.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final OrderRepository orderRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final RestaurantTableRepository tableRepository;

    @Autowired
    public AdminService(UserRepository userRepository,
                       StaffRepository staffRepository,
                       OrderRepository orderRepository,
                       InventoryItemRepository inventoryItemRepository,
                       MenuItemRepository menuItemRepository,
                       MenuCategoryRepository menuCategoryRepository,
                       RestaurantTableRepository tableRepository) {
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
        this.orderRepository = orderRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.menuItemRepository = menuItemRepository;
        this.menuCategoryRepository = menuCategoryRepository;
        this.tableRepository = tableRepository;
    }

    // Dashboard Statistics
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // User statistics
            long totalUsers = userRepository.count();
            long activeUsers = userRepository.count();
            
            // Staff statistics
            long totalStaff = staffRepository.count();
            long availableStaff = staffRepository.findByIsAvailableTrue().size();
            
            // Order statistics
            long totalOrders = orderRepository.count();
            long pendingOrders = orderRepository.findByStatus(Order.OrderStatus.PENDING).size();
            long completedOrders = orderRepository.findByStatus(Order.OrderStatus.COMPLETED).size();
            
            // Inventory statistics
            long totalInventoryItems = inventoryItemRepository.count();
            long lowStockItems = inventoryItemRepository.findLowStockItems().size();
            
            // Menu statistics
            long totalMenuItems = menuItemRepository.count();
            long availableMenuItems = menuItemRepository.findByIsAvailableTrue().size();
            
            // Table statistics
            long totalTables = tableRepository.count();
            
            stats.put("totalUsers", totalUsers);
            stats.put("activeUsers", activeUsers);
            stats.put("totalStaff", totalStaff);
            stats.put("availableStaff", availableStaff);
            stats.put("totalOrders", totalOrders);
            stats.put("pendingOrders", pendingOrders);
            stats.put("completedOrders", completedOrders);
            stats.put("totalInventoryItems", totalInventoryItems);
            stats.put("lowStockItems", lowStockItems);
            stats.put("totalMenuItems", totalMenuItems);
            stats.put("availableMenuItems", availableMenuItems);
            stats.put("totalTables", totalTables);
            
        } catch (Exception e) {
            System.err.println("Error loading dashboard stats: " + e.getMessage());
            // Set default values
            stats.put("totalUsers", 0);
            stats.put("activeUsers", 0);
            stats.put("totalStaff", 0);
            stats.put("availableStaff", 0);
            stats.put("totalOrders", 0);
            stats.put("pendingOrders", 0);
            stats.put("completedOrders", 0);
            stats.put("totalInventoryItems", 0);
            stats.put("lowStockItems", 0);
            stats.put("totalMenuItems", 0);
            stats.put("availableMenuItems", 0);
            stats.put("totalTables", 0);
        }
        
        return stats;
    }

    // User Management
    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            return List.of();
        }
    }

    public List<User> getUsersByRole(User.Role role) {
        try {
            return userRepository.findByRole(role);
        } catch (Exception e) {
            System.err.println("Error loading users by role: " + e.getMessage());
            return List.of();
        }
    }

    public User getUserById(Long id) {
        try {
            return userRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Error loading user by ID: " + e.getMessage());
            return null;
        }
    }

    public User updateUserRole(Long userId, User.Role newRole) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.setRole(newRole);
                user.setUpdatedAt(LocalDateTime.now());
                return userRepository.save(user);
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error updating user role: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteUser(Long userId) {
        try {
            userRepository.deleteById(userId);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    // Staff Management
    public List<Staff> getAllStaff() {
        try {
            return staffRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error loading staff: " + e.getMessage());
            return List.of();
        }
    }

    public List<Staff> getStaffByPosition(Staff.Position position) {
        try {
            return staffRepository.findByPosition(position);
        } catch (Exception e) {
            System.err.println("Error loading staff by position: " + e.getMessage());
            return List.of();
        }
    }

    // Tables Management
    public List<RestaurantTable> getAllTables() {
        try {
            return tableRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error loading tables: " + e.getMessage());
            return List.of();
        }
    }

    // Menu Management
    public List<MenuCategory> getActiveCategoriesOrdered() {
        try {
            return menuCategoryRepository.findActiveCategoriesOrdered();
        } catch (Exception e) {
            System.err.println("Error loading categories: " + e.getMessage());
            return List.of();
        }
    }

    public List<MenuItem> getAllMenuItems() {
        try {
            return menuItemRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error loading menu items: " + e.getMessage());
            return List.of();
        }
    }

    // System Reports
    public Map<String, Object> getSystemReport() {
        Map<String, Object> report = new HashMap<>();
        
        try {
            // Recent activity (last 24 hours)
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
            List<Order> recentOrders = orderRepository.findByCreatedAtBetween(yesterday, LocalDateTime.now());
            
            // User activity by role
            Map<User.Role, Long> usersByRole = userRepository.findAll().stream()
                .collect(Collectors.groupingBy(User::getRole, Collectors.counting()));
            
            // Staff by position
            Map<Staff.Position, Long> staffByPosition = staffRepository.findAll().stream()
                .collect(Collectors.groupingBy(Staff::getPosition, Collectors.counting()));
            
            // Order status distribution
            Map<Order.OrderStatus, Long> ordersByStatus = orderRepository.findAll().stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
            
            report.put("recentOrders", recentOrders.size());
            report.put("usersByRole", usersByRole);
            report.put("staffByPosition", staffByPosition);
            report.put("ordersByStatus", ordersByStatus);
            report.put("reportDate", LocalDateTime.now());
            
        } catch (Exception e) {
            System.err.println("Error generating system report: " + e.getMessage());
            report.put("error", "Failed to generate report: " + e.getMessage());
        }
        
        return report;
    }

    // System Health Check
    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Database connectivity
            long userCount = userRepository.count();
            health.put("databaseStatus", "Connected");
            health.put("databaseRecords", userCount);
            
            // System uptime (simplified)
            health.put("uptime", "System running");
            health.put("lastCheck", LocalDateTime.now());
            
            // Memory usage (simplified)
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            health.put("totalMemory", totalMemory);
            health.put("usedMemory", usedMemory);
            health.put("freeMemory", freeMemory);
            health.put("memoryUsagePercent", (usedMemory * 100) / totalMemory);
            
        } catch (Exception e) {
            System.err.println("Error checking system health: " + e.getMessage());
            health.put("databaseStatus", "Error");
            health.put("error", e.getMessage());
        }
        
        return health;
    }

    // Recent Activity
    public List<Map<String, Object>> getRecentActivity() {
        try {
            // This would typically come from an audit log table
            // For now, we'll create some sample activity data
            return List.of(
                Map.of("type", "User Login", "user", "admin@demo.com", "time", LocalDateTime.now().minusMinutes(5)),
                Map.of("type", "Order Created", "user", "customer@demo.com", "time", LocalDateTime.now().minusMinutes(10)),
                Map.of("type", "Inventory Updated", "user", "chef@demo.com", "time", LocalDateTime.now().minusMinutes(15)),
                Map.of("type", "System Backup", "user", "system", "time", LocalDateTime.now().minusHours(1))
            );
        } catch (Exception e) {
            System.err.println("Error loading recent activity: " + e.getMessage());
            return List.of();
        }
    }
}
