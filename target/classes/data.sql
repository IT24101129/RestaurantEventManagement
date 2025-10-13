-- Sample users for testing (password is 'demo123' encoded with BCrypt)
INSERT INTO users (name, email, password, phone, role, loyalty_points, last_points_update, created_at, updated_at) VALUES
('John Doe', 'customer@demo.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '1234567890', 'CUSTOMER', 0, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jane Manager', 'manager@demo.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '1234567891', 'RESTAURANT_MANAGER', 0, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Chef Smith', 'chef@demo.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '1234567892', 'HEAD_CHEF', 0, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Admin User', 'admin@demo.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '1234567893', 'ADMIN', 0, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample restaurant tables
INSERT INTO restaurant_tables (name, capacity, available) VALUES
('1', 2, TRUE),
('2', 4, TRUE),
('3', 4, TRUE),
('4', 6, TRUE),
('5', 8, TRUE),
('6', 10, TRUE);

-- Sample menu categories
INSERT INTO menu_categories (name, description, display_order, is_active, created_at, updated_at) VALUES
('Appetizers', 'Start your meal with our delicious appetizers', 1, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Main Courses', 'Our signature dishes that will satisfy your appetite', 2, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Desserts', 'Sweet endings to perfect your dining experience', 3, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Beverages', 'Refreshing drinks to complement your meal', 4, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample menu items
INSERT INTO menu_items (category_id, name, description, price, is_available, image_url, preparation_time, created_at, updated_at) VALUES
(1, 'Caesar Salad', 'Fresh romaine lettuce, parmesan cheese, croutons, and our signature Caesar dressing', 12.99, TRUE, NULL, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Buffalo Wings', 'Crispy chicken wings tossed in our spicy buffalo sauce, served with celery and blue cheese', 14.99, TRUE, NULL, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Grilled Salmon', 'Fresh Atlantic salmon grilled to perfection, served with seasonal vegetables and lemon butter sauce', 24.99, TRUE, NULL, 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Ribeye Steak', 'Premium ribeye steak grilled to your preference, served with mashed potatoes and grilled asparagus', 32.99, TRUE, NULL, 25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Chocolate Lava Cake', 'Warm chocolate cake with a molten center, served with vanilla ice cream and fresh berries', 8.99, TRUE, NULL, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Tiramisu', 'Classic Italian dessert with layers of coffee-soaked ladyfingers and mascarpone cream', 7.99, TRUE, NULL, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Fresh Orange Juice', 'Freshly squeezed orange juice, rich in vitamin C and natural sweetness', 4.99, TRUE, NULL, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Espresso', 'Rich, full-bodied espresso made from premium coffee beans', 3.99, TRUE, NULL, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);