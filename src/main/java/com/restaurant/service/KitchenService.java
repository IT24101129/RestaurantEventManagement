package com.restaurant.service;

import com.restaurant.model.*;
import com.restaurant.repository.KitchenTaskRepository;
import com.restaurant.repository.InventoryItemRepository;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class KitchenService {
    
    private final KitchenTaskRepository kitchenTaskRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final OrderRepository orderRepository;
    private final StaffRepository staffRepository;
    private final NotificationService notificationService;
    
    @Autowired
    public KitchenService(KitchenTaskRepository kitchenTaskRepository,
                         InventoryItemRepository inventoryItemRepository,
                         OrderRepository orderRepository,
                         StaffRepository staffRepository,
                         NotificationService notificationService) {
        this.kitchenTaskRepository = kitchenTaskRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.orderRepository = orderRepository;
        this.staffRepository = staffRepository;
        this.notificationService = notificationService;
    }
    
    // Order Management
    public List<Order> getPendingOrders() {
        try {
            return orderRepository.findByStatus(Order.OrderStatus.PENDING);
        } catch (Exception e) {
            System.err.println("Error getting pending orders: " + e.getMessage());
            return List.of();
        }
    }
    
    public List<Order> getInPreparationOrders() {
        try {
            return orderRepository.findByStatus(Order.OrderStatus.IN_PREPARATION);
        } catch (Exception e) {
            System.err.println("Error getting in-preparation orders: " + e.getMessage());
            return List.of();
        }
    }
    
    public List<Order> getReadyOrders() {
        try {
            return orderRepository.findByStatus(Order.OrderStatus.READY);
        } catch (Exception e) {
            System.err.println("Error getting ready orders: " + e.getMessage());
            return List.of();
        }
    }
    
    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(order);
        
        // Send notification when order is ready
        if (newStatus == Order.OrderStatus.READY) {
            notificationService.sendOrderReadyNotification(savedOrder);
        }
        
        return savedOrder;
    }
    
    // Kitchen Task Management
    public KitchenTask createKitchenTask(Order order, Staff staff, KitchenTask.TaskType taskType, 
                                       String itemName, Integer quantity, Integer estimatedDuration,
                                       KitchenTask.Priority priority, String dietaryNotes, 
                                       String specialInstructions, String createdBy) {
        
        KitchenTask task = new KitchenTask(order, staff, taskType, itemName, quantity, estimatedDuration);
        task.setPriority(priority);
        task.setDietaryNotes(dietaryNotes);
        task.setSpecialInstructions(specialInstructions);
        task.setCreatedBy(createdBy);
        
        return kitchenTaskRepository.save(task);
    }
    
    public List<KitchenTask> createTasksForOrder(Order order, String createdBy) {
        if (order == null || order.getOrderItems() == null) {
            return List.of();
        }
        
        List<KitchenTask> tasks = order.getOrderItems().stream()
            .map(orderItem -> {
                // Find available kitchen staff
                Staff availableStaff = findAvailableKitchenStaff();
                if (availableStaff == null) {
                    // Create task without staff assignment for now
                    availableStaff = new Staff();
                    availableStaff.setId(1L); // Temporary ID
                }
                
                // Determine task type based on menu item
                KitchenTask.TaskType taskType = determineTaskType(orderItem.getMenuItem());
                
                // Calculate estimated duration
                Integer estimatedDuration = calculateEstimatedDuration(orderItem.getMenuItem(), orderItem.getQuantity());
                
                // Determine priority
                KitchenTask.Priority priority = determinePriority(order);
                
                return createKitchenTask(
                    order, availableStaff, taskType, orderItem.getMenuItem().getName(),
                    orderItem.getQuantity(), estimatedDuration, priority,
                    orderItem.getSpecialRequests(), order.getSpecialInstructions(), createdBy
                );
            })
            .collect(Collectors.toList());
        
        // Update order status to "In Preparation"
        updateOrderStatus(order.getId(), Order.OrderStatus.IN_PREPARATION);
        
        return tasks;
    }
    
    public KitchenTask assignTaskToStaff(Long taskId, Long staffId) {
        KitchenTask task = kitchenTaskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        Staff staff = staffRepository.findById(staffId)
            .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        // Check if staff is available
        if (!isStaffAvailable(staff)) {
            throw new RuntimeException("Staff is not available for new tasks");
        }
        
        task.setStaff(staff);
        task.setStatus(KitchenTask.TaskStatus.PENDING);
        
        return kitchenTaskRepository.save(task);
    }
    
    public KitchenTask startTask(Long taskId) {
        KitchenTask task = kitchenTaskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        task.setStatus(KitchenTask.TaskStatus.IN_PROGRESS);
        task.setStartedAt(LocalDateTime.now());
        
        return kitchenTaskRepository.save(task);
    }
    
    public KitchenTask completeTask(Long taskId) {
        KitchenTask task = kitchenTaskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));
        
        task.setStatus(KitchenTask.TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        
        // Calculate actual duration
        if (task.getStartedAt() != null) {
            long duration = java.time.Duration.between(task.getStartedAt(), task.getCompletedAt()).toMinutes();
            task.setActualDurationMinutes((int) duration);
        }
        
        KitchenTask savedTask = kitchenTaskRepository.save(task);
        
        // Check if all tasks for the order are completed
        checkOrderCompletion(task.getOrder());
        
        return savedTask;
    }
    
    public List<KitchenTask> getPendingTasks() {
        try {
            return kitchenTaskRepository.findPendingTasksOrderedByPriority();
        } catch (Exception e) {
            System.err.println("Error getting pending tasks: " + e.getMessage());
            return List.of();
        }
    }
    
    public List<KitchenTask> getInProgressTasks() {
        try {
            return kitchenTaskRepository.findInProgressTasks();
        } catch (Exception e) {
            System.err.println("Error getting in-progress tasks: " + e.getMessage());
            return List.of();
        }
    }
    
    public List<KitchenTask> getTasksByOrder(Order order) {
        return kitchenTaskRepository.findTasksByOrderOrderedByPriority(order);
    }
    
    public List<KitchenTask> getTasksByStaff(Staff staff) {
        return kitchenTaskRepository.findActiveTasksByStaff(staff);
    }
    
    // Staff Management
    public List<Staff> getAvailableKitchenStaff() {
        try {
            return staffRepository.findByIsAvailableTrue().stream()
                .filter(staff -> staff.getPosition() == Staff.Position.CHEF || 
                                staff.getPosition() == Staff.Position.KITCHEN_STAFF ||
                                staff.getPosition() == Staff.Position.KITCHEN_ASSISTANT)
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting available kitchen staff: " + e.getMessage());
            return List.of();
        }
    }
    
    public Staff findAvailableKitchenStaff() {
        List<Staff> availableStaff = getAvailableKitchenStaff();
        
        // Find staff with least active tasks
        return availableStaff.stream()
            .min((s1, s2) -> {
                Long count1 = kitchenTaskRepository.countActiveTasksByStaff(s1);
                Long count2 = kitchenTaskRepository.countActiveTasksByStaff(s2);
                return count1.compareTo(count2);
            })
            .orElse(null);
    }
    
    public boolean isStaffAvailable(Staff staff) {
        Long activeTaskCount = kitchenTaskRepository.countActiveTasksByStaff(staff);
        return activeTaskCount < 3; // Maximum 3 active tasks per staff member
    }
    
    // Inventory Management
    public List<InventoryItem> getLowStockItems() {
        try {
            return inventoryItemRepository.findLowStockItemsOrderedByUrgency();
        } catch (Exception e) {
            System.err.println("Error getting low stock items: " + e.getMessage());
            return List.of();
        }
    }
    
    public List<InventoryItem> getAllInventoryItems() {
        return inventoryItemRepository.findAll();
    }
    
    public InventoryItem updateInventoryStock(Long itemId, Double newStock) {
        InventoryItem item = inventoryItemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Inventory item not found"));
        
        item.setCurrentStock(newStock);
        item.setLastRestocked(LocalDateTime.now());
        
        return inventoryItemRepository.save(item);
    }
    
    public void checkLowStockAndNotify() {
        List<InventoryItem> lowStockItems = getLowStockItems();
        
        if (!lowStockItems.isEmpty()) {
            // Send notification to Head Chef
            notificationService.sendLowStockAlert(lowStockItems);
            
            // Auto-request restocking
            requestRestocking(lowStockItems);
        }
    }
    
    // Delay Management
    public List<KitchenTask> getDelayedTasks() {
        try {
            LocalDateTime threshold = LocalDateTime.now().minusHours(1); // Tasks running for more than 1 hour
            return kitchenTaskRepository.findDelayedTasks(threshold);
        } catch (Exception e) {
            System.err.println("Error getting delayed tasks: " + e.getMessage());
            return List.of();
        }
    }
    
    public void handleTaskDelay(KitchenTask task) {
        task.setStatus(KitchenTask.TaskStatus.DELAYED);
        kitchenTaskRepository.save(task);
        
        // Notify front-end staff about delay
        notificationService.sendTaskDelayNotification(task);
    }
    
    // Helper Methods
    private KitchenTask.TaskType determineTaskType(MenuItem menuItem) {
        // Simple logic to determine task type based on menu item
        if (menuItem.getName().toLowerCase().contains("salad")) {
            return KitchenTask.TaskType.PREP;
        } else if (menuItem.getName().toLowerCase().contains("soup")) {
            return KitchenTask.TaskType.COOK;
        } else {
            return KitchenTask.TaskType.COOK;
        }
    }
    
    private Integer calculateEstimatedDuration(MenuItem menuItem, Integer quantity) {
        // Base preparation time + quantity multiplier
        int baseTime = menuItem.getPreparationTime() != null ? menuItem.getPreparationTime() : 15;
        return baseTime + (quantity - 1) * 5; // 5 minutes per additional item
    }
    
    private KitchenTask.Priority determinePriority(Order order) {
        // Determine priority based on order characteristics
        if (order.getOrderType() == Order.OrderType.DINE_IN) {
            return KitchenTask.Priority.HIGH;
        } else if (order.getOrderType() == Order.OrderType.TAKEAWAY) {
            return KitchenTask.Priority.MEDIUM;
        } else {
            return KitchenTask.Priority.LOW;
        }
    }
    
    private void checkOrderCompletion(Order order) {
        List<KitchenTask> pendingTasks = kitchenTaskRepository.findTasksByOrderAndStatus(
            order, KitchenTask.TaskStatus.PENDING);
        List<KitchenTask> inProgressTasks = kitchenTaskRepository.findTasksByOrderAndStatus(
            order, KitchenTask.TaskStatus.IN_PROGRESS);
        
        if (pendingTasks.isEmpty() && inProgressTasks.isEmpty()) {
            // All tasks completed, mark order as ready
            updateOrderStatus(order.getId(), Order.OrderStatus.READY);
        }
    }
    
    private void requestRestocking(List<InventoryItem> lowStockItems) {
        // This would typically integrate with a supplier system
        // For now, we'll just log the request
        System.out.println("Auto-requesting restocking for: " + 
            lowStockItems.stream().map(InventoryItem::getItemName).collect(Collectors.joining(", ")));
    }
}
