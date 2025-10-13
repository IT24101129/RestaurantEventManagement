package com.restaurant.config;

import com.restaurant.model.User;
import com.restaurant.model.Staff;
import com.restaurant.model.InventoryItem;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.repository.UserRepository;
import com.restaurant.repository.RestaurantTableRepository;
import com.restaurant.repository.MenuCategoryRepository;
import com.restaurant.repository.MenuItemRepository;
import com.restaurant.repository.StaffRepository;
import com.restaurant.repository.InventoryItemRepository;
import com.restaurant.repository.OrderRepository;
import com.restaurant.model.RestaurantTable;
import com.restaurant.model.MenuCategory;
import com.restaurant.model.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RestaurantTableRepository tableRepository;
    
    @Autowired
    private MenuCategoryRepository menuCategoryRepository;
    
    @Autowired
    private MenuItemRepository menuItemRepository;
    
    @Autowired
    private StaffRepository staffRepository;
    
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeDemoUsers();
        initializeRestaurantTables();
        initializeMenuData();
        initializeKitchenData();
    }

    private void initializeDemoUsers() {
        // Check if users already exist
        if (userRepository.count() > 0) {
            return;
        }

        // Create demo users
        User customer = new User();
        customer.setName("John Doe");
        customer.setEmail("customer@demo.com");
        customer.setPassword(passwordEncoder.encode("demo123"));
        customer.setPhone("1234567890");
        customer.setRole(User.Role.CUSTOMER);
        customer.setLoyaltyPoints(0);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        userRepository.save(customer);

        User manager = new User();
        manager.setName("Jane Manager");
        manager.setEmail("manager@demo.com");
        manager.setPassword(passwordEncoder.encode("demo123"));
        manager.setPhone("1234567891");
        manager.setRole(User.Role.RESTAURANT_MANAGER);
        manager.setLoyaltyPoints(0);
        manager.setCreatedAt(LocalDateTime.now());
        manager.setUpdatedAt(LocalDateTime.now());
        userRepository.save(manager);

        User chef = new User();
        chef.setName("Chef Smith");
        chef.setEmail("chef@demo.com");
        chef.setPassword(passwordEncoder.encode("demo123"));
        chef.setPhone("1234567892");
        chef.setRole(User.Role.HEAD_CHEF);
        chef.setLoyaltyPoints(0);
        chef.setCreatedAt(LocalDateTime.now());
        chef.setUpdatedAt(LocalDateTime.now());
        userRepository.save(chef);

        User admin = new User();
        admin.setName("Admin User");
        admin.setEmail("admin@demo.com");
        admin.setPassword(passwordEncoder.encode("demo123"));
        admin.setPhone("1234567893");
        admin.setRole(User.Role.ADMIN);
        admin.setLoyaltyPoints(0);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        userRepository.save(admin);

        System.out.println("✅ Demo users initialized successfully!");
    }

    private void initializeRestaurantTables() {
        // Check if tables already exist
        if (tableRepository.count() > 0) {
            return;
        }

        // Create sample tables
        for (int i = 1; i <= 6; i++) {
            RestaurantTable table = new RestaurantTable();
            table.setName(String.valueOf(i));
            table.setCapacity(i == 1 ? 2 : (i <= 3 ? 4 : (i <= 4 ? 6 : (i == 5 ? 8 : 10))));
            table.setAvailable(true);
            tableRepository.save(table);
        }

        System.out.println("✅ Restaurant tables initialized successfully!");
    }

    private void initializeMenuData() {
        // Check if menu data already exists
        if (menuCategoryRepository.count() > 0) {
            return;
        }

        // Create menu categories
        MenuCategory appetizers = new MenuCategory();
        appetizers.setName("Appetizers");
        appetizers.setDescription("Start your meal with our delicious appetizers");
        appetizers.setDisplayOrder(1);
        appetizers.setIsActive(true);
        appetizers.setCreatedAt(LocalDateTime.now());
        appetizers.setUpdatedAt(LocalDateTime.now());
        menuCategoryRepository.save(appetizers);

        MenuCategory mainCourses = new MenuCategory();
        mainCourses.setName("Main Courses");
        mainCourses.setDescription("Our signature dishes that will satisfy your appetite");
        mainCourses.setDisplayOrder(2);
        mainCourses.setIsActive(true);
        mainCourses.setCreatedAt(LocalDateTime.now());
        mainCourses.setUpdatedAt(LocalDateTime.now());
        menuCategoryRepository.save(mainCourses);

        MenuCategory desserts = new MenuCategory();
        desserts.setName("Desserts");
        desserts.setDescription("Sweet endings to perfect your dining experience");
        desserts.setDisplayOrder(3);
        desserts.setIsActive(true);
        desserts.setCreatedAt(LocalDateTime.now());
        desserts.setUpdatedAt(LocalDateTime.now());
        menuCategoryRepository.save(desserts);

        MenuCategory beverages = new MenuCategory();
        beverages.setName("Beverages");
        beverages.setDescription("Refreshing drinks to complement your meal");
        beverages.setDisplayOrder(4);
        beverages.setIsActive(true);
        beverages.setCreatedAt(LocalDateTime.now());
        beverages.setUpdatedAt(LocalDateTime.now());
        menuCategoryRepository.save(beverages);

        // Create menu items
        createMenuItem(appetizers, "Caesar Salad", "Fresh romaine lettuce, parmesan cheese, croutons, and our signature Caesar dressing", 12.99, 10);
        createMenuItem(appetizers, "Buffalo Wings", "Crispy chicken wings tossed in our spicy buffalo sauce, served with celery and blue cheese", 14.99, 15);
        
        createMenuItem(mainCourses, "Grilled Salmon", "Fresh Atlantic salmon grilled to perfection, served with seasonal vegetables and lemon butter sauce", 24.99, 20);
        createMenuItem(mainCourses, "Ribeye Steak", "Premium ribeye steak grilled to your preference, served with mashed potatoes and grilled asparagus", 32.99, 25);
        
        createMenuItem(desserts, "Chocolate Lava Cake", "Warm chocolate cake with a molten center, served with vanilla ice cream and fresh berries", 8.99, 12);
        createMenuItem(desserts, "Tiramisu", "Classic Italian dessert with layers of coffee-soaked ladyfingers and mascarpone cream", 7.99, 5);
        
        createMenuItem(beverages, "Fresh Orange Juice", "Freshly squeezed orange juice, rich in vitamin C and natural sweetness", 4.99, 3);
        createMenuItem(beverages, "Espresso", "Rich, full-bodied espresso made from premium coffee beans", 3.99, 2);

        System.out.println("✅ Menu data initialized successfully!");
    }

    private void createMenuItem(MenuCategory category, String name, String description, double price, int prepTime) {
        MenuItem item = new MenuItem();
        item.setCategory(category);
        item.setName(name);
        item.setDescription(description);
        item.setPrice(BigDecimal.valueOf(price));
        item.setIsAvailable(true);
        item.setPreparationTime(prepTime);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        menuItemRepository.save(item);
    }

    private void initializeKitchenData() {
        // Check if kitchen data already exists
        if (staffRepository.count() > 0) {
            return;
        }

        System.out.println("🍳 Initializing kitchen data...");

        // Create kitchen staff
        createStaff("Chef Smith", "chef@demo.com", "555-0101", Staff.Position.CHEF);
        createStaff("Kitchen Assistant", "kitchen@demo.com", "555-0102", Staff.Position.KITCHEN_ASSISTANT);
        createStaff("Prep Cook", "prep@demo.com", "555-0103", Staff.Position.KITCHEN_STAFF);

        // Create inventory items
        createInventoryItem("Fresh Salmon", "Fresh Atlantic salmon fillets", InventoryItem.Category.PROTEIN, 50.0, 10.0, 100.0, "kg", "Ocean Fresh", 12.99, true);
        createInventoryItem("Ribeye Steak", "Premium ribeye steaks", InventoryItem.Category.PROTEIN, 30.0, 5.0, 60.0, "pieces", "Prime Meats", 15.99, true);
        createInventoryItem("Potatoes", "Fresh russet potatoes", InventoryItem.Category.VEGETABLE, 100.0, 20.0, 200.0, "kg", "Farm Fresh", 2.49, true);
        createInventoryItem("Chocolate", "Dark chocolate for desserts", InventoryItem.Category.DESSERT, 25.0, 5.0, 50.0, "kg", "Sweet Supplies", 8.99, true);
        createInventoryItem("Coffee Beans", "Premium coffee beans", InventoryItem.Category.BEVERAGES, 15.0, 3.0, 30.0, "kg", "Coffee Co", 6.99, true);

        // Create sample orders
        createSampleOrder();

        System.out.println("✅ Kitchen data initialized successfully!");
    }

    private void createStaff(String name, String email, String phone, Staff.Position position) {
        Staff staff = new Staff();
        staff.setName(name);
        staff.setEmail(email);
        staff.setPhone(phone);
        staff.setPosition(position);
        staff.setIsAvailable(true);
        staff.setCreatedAt(LocalDateTime.now());
        staff.setUpdatedAt(LocalDateTime.now());
        
        staffRepository.save(staff);
    }

    private void createInventoryItem(String name, String description, InventoryItem.Category category, 
                                   Double currentStock, Double minimumStock, Double maximumStock, String unit, 
                                   String supplier, Double unitPrice, Boolean isLowStock) {
        InventoryItem item = new InventoryItem();
        item.setItemName(name);
        item.setDescription(description);
        item.setCategory(category);
        item.setMinimumStock(minimumStock);
        item.setMaximumStock(maximumStock);
        item.setCurrentStock(currentStock);
        item.setUnit(unit);
        item.setUnitPrice(unitPrice);
        item.setSupplier(supplier);
        item.setIsLowStock(isLowStock);
        item.setLastRestocked(LocalDateTime.now());
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        
        inventoryItemRepository.save(item);
    }

    private void createSampleOrder() {
        // Get a customer user
        User customer = userRepository.findByEmail("customer@demo.com").orElse(null);
        if (customer == null) return;

        // Create a sample order
        Order order = new Order();
        order.setUser(customer);
        order.setOrderType(Order.OrderType.DINE_IN);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.valueOf(45.98));
        order.setPaymentStatus(Order.PaymentStatus.PENDING);
        order.setSpecialInstructions("No onions on the salad");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);
    }
}
