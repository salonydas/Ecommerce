# Ecommerce
any query for the database check the below statement
DROP DATABASE IF EXISTS ecommerce;

-- Create new database
CREATE DATABASE ecommerce;
USE ecommerce;

-- Users table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Categories table
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Products table without imageURL column
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    category_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Orders table
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'PLACED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Order items table
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Cart items table
CREATE TABLE cart_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    UNIQUE KEY user_product (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Insert sample categories
INSERT INTO categories(name) VALUES 
('Electronics'), 
('Books'), 
('Home Appliances');

-- Insert sample products without image URLs
INSERT INTO products(name, description, price, stock, category_id) VALUES
('Bluetooth Headphones', 'Noise-cancelling over-ear headphones', 1299.00, 30, 1),
('Smartphone', 'Latest model with 128GB storage', 19999.00, 15, 1),
('Python Programming Book', 'Beginner to advanced Python', 699.00, 25, 2),
('Data Structures Book', 'Comprehensive guide to data structures', 599.00, 30, 2),
('Microwave Oven', '700W compact microwave oven', 3999.00, 8, 3),
('Electric Kettle', '1.5L fast boiling kettle', 999.00, 20, 3),
('Gaming Keyboard', 'Mechanical RGB keyboard', 1499.00, 40, 1),
('Office Chair', 'Ergonomic swivel chair', 5499.00, 12, 3),
('Database Systems Book', 'Learn MySQL and PostgreSQL', 899.00, 18, 2),
('Air Fryer', 'Oil-less fryer for healthy cooking', 3499.00, 10, 3);
