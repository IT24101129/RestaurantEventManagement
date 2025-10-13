-- Restaurant Management System Database Schema
-- H2 Database compatible version

-- Drop existing tables if they exist
DROP TABLE IF EXISTS satisfaction_reports;
DROP TABLE IF EXISTS feedback_responses;
DROP TABLE IF EXISTS customer_feedback;
DROP TABLE IF EXISTS event_equipment;
DROP TABLE IF EXISTS event_staff_assignments;
DROP TABLE IF EXISTS event_bookings;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS kitchen_tasks;
DROP TABLE IF EXISTS inventory_items;
DROP TABLE IF EXISTS schedules;
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS menu_categories;
DROP TABLE IF EXISTS restaurant_tables;
DROP TABLE IF EXISTS users;

-- Create users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(10),
    role VARCHAR(50) NOT NULL,
    loyalty_points INT DEFAULT 0,
    last_points_update TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create restaurant_tables table
CREATE TABLE restaurant_tables (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create menu_categories table
CREATE TABLE menu_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create menu_items table
CREATE TABLE menu_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    price DECIMAL(10,2) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    image_url VARCHAR(500),
    preparation_time INT DEFAULT 15,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES menu_categories(id)
);

-- Create orders table
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(20),
    table_id BIGINT,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (table_id) REFERENCES restaurant_tables(id)
);

-- Create order_items table
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    special_instructions VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);

-- Create reservations table
CREATE TABLE reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    customer_name VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,
    customer_email VARCHAR(255),
    table_id BIGINT NOT NULL,
    reservation_date_time TIMESTAMP NOT NULL,
    party_size INT NOT NULL,
    special_requests VARCHAR(1000),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (table_id) REFERENCES restaurant_tables(id)
);

-- Create staff table
CREATE TABLE staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    position VARCHAR(50) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    max_hours_per_week INT DEFAULT 40,
    preferred_shift_start VARCHAR(10),
    preferred_shift_end VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create schedules table
CREATE TABLE schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    staff_id BIGINT NOT NULL,
    schedule_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    shift_type VARCHAR(50) NOT NULL,
    notes VARCHAR(1000),
    status VARCHAR(20) DEFAULT 'SCHEDULED',
    created_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (staff_id) REFERENCES staff(id)
);

-- Create inventory_items table
CREATE TABLE inventory_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    current_stock DECIMAL(10,2) NOT NULL,
    minimum_stock DECIMAL(10,2) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    unit_cost DECIMAL(10,2) NOT NULL,
    supplier VARCHAR(255),
    last_restocked TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create kitchen_tasks table
CREATE TABLE kitchen_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    task_name VARCHAR(255) NOT NULL,
    assigned_to BIGINT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    estimated_time INT,
    actual_time INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (assigned_to) REFERENCES staff(id)
);

-- Create events table
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_name VARCHAR(255) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    description VARCHAR(1000),
    capacity INT NOT NULL,
    duration_hours INT NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    special_requirements VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create event_bookings table
CREATE TABLE event_bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    client_name VARCHAR(255) NOT NULL,
    client_email VARCHAR(255) NOT NULL,
    client_phone VARCHAR(50) NOT NULL,
    event_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    guest_count INT NOT NULL,
    special_requirements VARCHAR(1000),
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    notes VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    FOREIGN KEY (event_id) REFERENCES events(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create event_staff_assignments table
CREATE TABLE event_staff_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_booking_id BIGINT NOT NULL,
    staff_id BIGINT NOT NULL,
    role VARCHAR(100) NOT NULL,
    assigned_hours INT,
    start_time VARCHAR(10),
    end_time VARCHAR(10),
    status VARCHAR(50) NOT NULL DEFAULT 'ASSIGNED',
    notes VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR(255),
    FOREIGN KEY (event_booking_id) REFERENCES event_bookings(id),
    FOREIGN KEY (staff_id) REFERENCES staff(id)
);

-- Create event_equipment table
CREATE TABLE event_equipment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_booking_id BIGINT NOT NULL,
    equipment_name VARCHAR(255) NOT NULL,
    equipment_type VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    unit_cost DECIMAL(10,2) NOT NULL,
    total_cost DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'REQUESTED',
    notes VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR(255),
    FOREIGN KEY (event_booking_id) REFERENCES event_bookings(id)
);

-- Create customer_feedback table
CREATE TABLE customer_feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT,
    customer_name VARCHAR(255),
    customer_email VARCHAR(255),
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    feedback_type VARCHAR(50) NOT NULL,
    subject VARCHAR(255),
    comment VARCHAR(1000),
    order_id BIGINT,
    reservation_id BIGINT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    is_escalated BOOLEAN NOT NULL DEFAULT FALSE,
    escalation_reason VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id)
);

-- Create feedback_responses table
CREATE TABLE feedback_responses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    feedback_id BIGINT NOT NULL,
    responded_by BIGINT,
    responder_name VARCHAR(255) NOT NULL,
    response_type VARCHAR(50) NOT NULL,
    response_text VARCHAR(1000) NOT NULL,
    is_internal BOOLEAN NOT NULL DEFAULT FALSE,
    is_escalated BOOLEAN NOT NULL DEFAULT FALSE,
    escalation_notes VARCHAR(1000),
    follow_up_required BOOLEAN NOT NULL DEFAULT FALSE,
    follow_up_date TIMESTAMP,
    promotional_offer_sent BOOLEAN NOT NULL DEFAULT FALSE,
    offer_details VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (feedback_id) REFERENCES customer_feedback(id),
    FOREIGN KEY (responded_by) REFERENCES users(id)
);

-- Create satisfaction_reports table
CREATE TABLE satisfaction_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    report_date DATE NOT NULL,
    report_type VARCHAR(50) NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    total_feedback_count INT NOT NULL DEFAULT 0,
    average_rating DECIMAL(3,2),
    high_rating_count INT NOT NULL DEFAULT 0,
    medium_rating_count INT NOT NULL DEFAULT 0,
    low_rating_count INT NOT NULL DEFAULT 0,
    resolved_issues_count INT NOT NULL DEFAULT 0,
    escalated_issues_count INT NOT NULL DEFAULT 0,
    critical_issues_count INT NOT NULL DEFAULT 0,
    promotional_offers_sent INT NOT NULL DEFAULT 0,
    satisfaction_score DECIMAL(3,2),
    summary VARCHAR(1000),
    critical_issues_summary VARCHAR(1000),
    recommendations VARCHAR(1000),
    status VARCHAR(50) NOT NULL DEFAULT 'GENERATED',
    generated_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
