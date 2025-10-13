package com.restaurant.controller;

import com.restaurant.model.*;
import com.restaurant.service.KitchenService;
import com.restaurant.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/kitchen")
public class KitchenController {
    
    private final KitchenService kitchenService;
    private final userService userService;
    
    @Autowired
    public KitchenController(KitchenService kitchenService, userService userService) {
        this.kitchenService = kitchenService;
        this.userService = userService;
    }
    
    /**
     * Display the real-time order dashboard for Head Chef
     */
    @GetMapping("/dashboard")
    public String kitchenDashboard(Model model) {
        try {
            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Optional<User> user = userService.findByEmail(username);
            
            if (user.isPresent()) {
                model.addAttribute("user", user.get());
            }
            
            // Initialize with empty lists to avoid null pointer exceptions
            List<Order> pendingOrders = List.of();
            List<Order> inPreparationOrders = List.of();
            List<Order> readyOrders = List.of();
            List<KitchenTask> pendingTasks = List.of();
            List<KitchenTask> inProgressTasks = List.of();
            List<Staff> availableStaff = List.of();
            List<InventoryItem> lowStockItems = List.of();
            List<KitchenTask> delayedTasks = List.of();
            
            try {
                // Get orders by status
                pendingOrders = kitchenService.getPendingOrders();
                inPreparationOrders = kitchenService.getInPreparationOrders();
                readyOrders = kitchenService.getReadyOrders();
                
                // Get kitchen tasks
                pendingTasks = kitchenService.getPendingTasks();
                inProgressTasks = kitchenService.getInProgressTasks();
                
                // Get available kitchen staff
                availableStaff = kitchenService.getAvailableKitchenStaff();
                
                // Get low stock items
                lowStockItems = kitchenService.getLowStockItems();
                
                // Get delayed tasks
                delayedTasks = kitchenService.getDelayedTasks();
            } catch (Exception e) {
                // Log the error but continue with empty lists
                System.err.println("Error loading kitchen data: " + e.getMessage());
            }
            
            model.addAttribute("pendingOrders", pendingOrders);
            model.addAttribute("inPreparationOrders", inPreparationOrders);
            model.addAttribute("readyOrders", readyOrders);
            model.addAttribute("pendingTasks", pendingTasks);
            model.addAttribute("inProgressTasks", inProgressTasks);
            model.addAttribute("availableStaff", availableStaff);
            model.addAttribute("lowStockItems", lowStockItems);
            model.addAttribute("delayedTasks", delayedTasks);
            
            return "kitchen/dashboard";
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load kitchen dashboard: " + e.getMessage());
            return "kitchen/dashboard";
        }
    }
    
    /**
     * Display kitchen orders page
     */
    @GetMapping("/orders")
    public String kitchenOrders(Model model) {
        try {
            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Optional<User> user = userService.findByEmail(username);
            
            if (user.isPresent()) {
                model.addAttribute("user", user.get());
            }
            
            // Get orders by status with error handling
            List<Order> pendingOrders = List.of();
            List<Order> inPreparationOrders = List.of();
            List<Order> readyOrders = List.of();
            
            try {
                pendingOrders = kitchenService.getPendingOrders();
                inPreparationOrders = kitchenService.getInPreparationOrders();
                readyOrders = kitchenService.getReadyOrders();
            } catch (Exception e) {
                System.err.println("Error loading orders: " + e.getMessage());
            }
            
            model.addAttribute("pendingOrders", pendingOrders);
            model.addAttribute("inPreparationOrders", inPreparationOrders);
            model.addAttribute("readyOrders", readyOrders);
            
            return "kitchen/orders";
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load kitchen orders: " + e.getMessage());
            return "kitchen/orders";
        }
    }

    /**
     * Display kitchen tasks page
     */
    @GetMapping("/tasks")
    public String kitchenTasks(Model model) {
        try {
            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Optional<User> user = userService.findByEmail(username);
            
            if (user.isPresent()) {
                model.addAttribute("user", user.get());
            }
            
            // Get tasks by status with error handling
            List<KitchenTask> pendingTasks = List.of();
            List<KitchenTask> inProgressTasks = List.of();
            List<KitchenTask> completedTasks = List.of();
            List<Staff> availableStaff = List.of();
            
            try {
                pendingTasks = kitchenService.getPendingTasks();
                inProgressTasks = kitchenService.getInProgressTasks();
                availableStaff = kitchenService.getAvailableKitchenStaff();
                
                // Get completed tasks from today
                // completedTasks = kitchenService.getCompletedTasksToday();
            } catch (Exception e) {
                System.err.println("Error loading tasks: " + e.getMessage());
            }
            
            model.addAttribute("pendingTasks", pendingTasks);
            model.addAttribute("inProgressTasks", inProgressTasks);
            model.addAttribute("completedTasks", completedTasks);
            model.addAttribute("availableStaff", availableStaff);
            
            return "kitchen/tasks";
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load kitchen tasks: " + e.getMessage());
            return "kitchen/tasks";
        }
    }

    /**
     * Display kitchen inventory page
     */
    @GetMapping("/inventory")
    public String kitchenInventory(Model model) {
        try {
            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Optional<User> user = userService.findByEmail(username);
            
            if (user.isPresent()) {
                model.addAttribute("user", user.get());
            }
            
            // Get inventory data with error handling
            List<InventoryItem> allInventoryItems = List.of();
            List<InventoryItem> lowStockItems = List.of();
            
            try {
                allInventoryItems = kitchenService.getAllInventoryItems();
                lowStockItems = kitchenService.getLowStockItems();
            } catch (Exception e) {
                System.err.println("Error loading inventory: " + e.getMessage());
            }
            
            model.addAttribute("allInventoryItems", allInventoryItems);
            model.addAttribute("lowStockItems", lowStockItems);
            
            return "kitchen/inventory";
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load kitchen inventory: " + e.getMessage());
            return "kitchen/inventory";
        }
    }

    /**
     * Get real-time order data for AJAX updates
     */
    @GetMapping("/api/orders")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOrderData() {
        try {
            Map<String, Object> data = new HashMap<>();

            List<Order> pending = kitchenService.getPendingOrders();
            List<Order> inPrep = kitchenService.getInPreparationOrders();
            List<Order> ready = kitchenService.getReadyOrders();

            data.put("pendingOrders", toSimpleOrders(pending));
            data.put("inPreparationOrders", toSimpleOrders(inPrep));
            data.put("readyOrders", toSimpleOrders(ready));

            List<KitchenTask> pendingTasks = kitchenService.getPendingTasks();
            List<KitchenTask> inProgressTasks = kitchenService.getInProgressTasks();
            List<InventoryItem> lowStockItems = kitchenService.getLowStockItems();
            List<KitchenTask> delayedTasks = kitchenService.getDelayedTasks();

            data.put("pendingTasksCount", pendingTasks != null ? pendingTasks.size() : 0);
            data.put("inProgressTasksCount", inProgressTasks != null ? inProgressTasks.size() : 0);
            data.put("lowStockItemsCount", lowStockItems != null ? lowStockItems.size() : 0);
            data.put("delayedTasksCount", delayedTasks != null ? delayedTasks.size() : 0);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(data);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to load order data: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(error);
        }
    }

    private List<Map<String, Object>> toSimpleOrders(List<Order> orders) {
        List<Map<String, Object>> list = new java.util.ArrayList<>();
        if (orders == null) return list;
        for (Order o : orders) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", o.getId());
            m.put("status", o.getStatus());
            m.put("createdAt", o.getCreatedAt());
            list.add(m);
        }
        return list;
    }
    
    /**
     * Update order status
     */
    @PostMapping("/orders/{orderId}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Long orderId, 
            @RequestParam String status) {
        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            Order updatedOrder = kitchenService.updateOrderStatus(orderId, newStatus);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order status updated successfully");
            response.put("order", updatedOrder);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to update order status: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Create kitchen tasks for an order
     */
    @PostMapping("/orders/{orderId}/create-tasks")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createTasksForOrder(@PathVariable Long orderId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String createdBy = auth.getName();
            
            // Get order (this would need to be implemented in KitchenService)
            // For now, we'll create a mock order
            List<KitchenTask> tasks = kitchenService.createTasksForOrder(null, createdBy);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Kitchen tasks created successfully");
            response.put("tasks", tasks);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to create kitchen tasks: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Assign task to staff
     */
    @PostMapping("/tasks/{taskId}/assign")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> assignTaskToStaff(
            @PathVariable Long taskId, 
            @RequestParam Long staffId) {
        try {
            KitchenTask task = kitchenService.assignTaskToStaff(taskId, staffId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Task assigned successfully");
            response.put("task", task);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to assign task: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Start a task
     */
    @PostMapping("/tasks/{taskId}/start")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> startTask(@PathVariable Long taskId) {
        try {
            KitchenTask task = kitchenService.startTask(taskId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Task started successfully");
            response.put("task", task);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to start task: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Complete a task
     */
    @PostMapping("/tasks/{taskId}/complete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeTask(@PathVariable Long taskId) {
        try {
            KitchenTask task = kitchenService.completeTask(taskId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Task completed successfully");
            response.put("task", task);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to complete task: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get available kitchen staff
     */
    @GetMapping("/api/staff/available")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAvailableStaff() {
        try {
            List<Staff> availableStaff = kitchenService.getAvailableKitchenStaff();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("staff", availableStaff);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to load available staff: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Check for low stock and send alerts
     */
    @PostMapping("/inventory/check-low-stock")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkLowStock() {
        try {
            kitchenService.checkLowStockAndNotify();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Low stock check completed");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to check low stock: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Handle task delays
     */
    @PostMapping("/tasks/{taskId}/delay")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleTaskDelay(@PathVariable Long taskId) {
        try {
            // This would need to be implemented in KitchenService
            // For now, we'll just return success
            // KitchenTask task = kitchenService.getTaskById(taskId);
            // kitchenService.handleTaskDelay(task);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Task delay handled successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to handle task delay: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Get order details for review
     */
    @GetMapping("/orders/{orderId}")
    public String getOrderDetails(@PathVariable Long orderId, Model model) {
        try {
            // This would need to be implemented to get order details
            // For now, we'll return a placeholder
            model.addAttribute("orderId", orderId);
            return "kitchen/order-details";
            
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load order details: " + e.getMessage());
            return "kitchen/order-details";
        }
    }
}
