-- Restaurant Management System Database Schema

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    email NVARCHAR(255) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    phone NVARCHAR(10),
    role NVARCHAR(50) NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);

-- Create restaurant_tables table
CREATE TABLE IF NOT EXISTS restaurant_tables (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    available BIT NOT NULL DEFAULT 1
);

-- Create menu_categories table
CREATE TABLE IF NOT EXISTS menu_categories (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(500),
    display_order INT DEFAULT 0,
    is_active BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);

-- Create menu_items table
CREATE TABLE IF NOT EXISTS menu_items (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    is_available BIT DEFAULT 1,
    image_url NVARCHAR(500),
    preparation_time INT DEFAULT 15,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (category_id) REFERENCES menu_categories(id)
);

-- Create reservations table
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    table_id BIGINT NOT NULL,
    reservation_datetime DATETIME2 NOT NULL,
    number_of_guests INT NOT NULL,
    status NVARCHAR(50) NOT NULL DEFAULT 'PENDING',
    special_requests NVARCHAR(500),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (table_id) REFERENCES restaurant_tables(id)
);

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_type NVARCHAR(50) NOT NULL DEFAULT 'DINE_IN',
    status NVARCHAR(50) NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    payment_status NVARCHAR(50) NOT NULL DEFAULT 'PENDING',
    delivery_address NVARCHAR(500),
    special_instructions NVARCHAR(500),
    estimated_delivery_time DATETIME2,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create order_items table
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);

-- Insert sample data
-- Sample users
INSERT INTO users (name, email, password, phone, role) VALUES
('John Doe', 'customer@demo.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '1234567890', 'CUSTOMER'),
('Jane Manager', 'manager@demo.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '1234567891', 'RESTAURANT_MANAGER'),
('Chef Smith', 'chef@demo.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '1234567892', 'HEAD_CHEF'),
('Admin User', 'admin@demo.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '1234567893', 'ADMIN');

-- Sample restaurant tables
INSERT INTO restaurant_tables (name, capacity, available) VALUES
('1', 2, 1),
('2', 4, 1),
('3', 4, 1),
('4', 6, 1),
('5', 8, 1),
('6', 10, 1);

-- Sample menu categories
INSERT INTO menu_categories (name, description, display_order) VALUES
('Appetizers', 'Start your meal with our delicious appetizers', 1),
('Main Courses', 'Our signature dishes that will satisfy your appetite', 2),
('Desserts', 'Sweet endings to perfect your dining experience', 3),
('Beverages', 'Refreshing drinks to complement your meal', 4);

-- Sample menu items
INSERT INTO menu_items (category_id, name, description, price, preparation_time) VALUES
(1, 'Caesar Salad', 'Fresh romaine lettuce, parmesan cheese, croutons, and our signature Caesar dressing', 12.99, 10),
(1, 'Buffalo Wings', 'Crispy chicken wings tossed in our spicy buffalo sauce, served with celery and blue cheese', 14.99, 15),
(2, 'Grilled Salmon', 'Fresh Atlantic salmon grilled to perfection, served with seasonal vegetables and lemon butter sauce', 24.99, 20),
(2, 'Ribeye Steak', 'Premium ribeye steak grilled to your preference, served with mashed potatoes and grilled asparagus', 32.99, 25),
(3, 'Chocolate Lava Cake', 'Warm chocolate cake with a molten center, served with vanilla ice cream and fresh berries', 8.99, 12),
(3, 'Tiramisu', 'Classic Italian dessert with layers of coffee-soaked ladyfingers and mascarpone cream', 7.99, 5),
(4, 'Fresh Orange Juice', 'Freshly squeezed orange juice, rich in vitamin C and natural sweetness', 4.99, 3),
(4, 'Espresso', 'Rich, full-bodied espresso made from premium coffee beans', 3.99, 2);
