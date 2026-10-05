-- MySQL Schema and Seed Data for Expiry Finder
CREATE DATABASE IF NOT EXISTS expiry_finder;
USE expiry_finder;

-- 1. Categories Table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Products Table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL,
    category_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Stock Batches Table
CREATE TABLE IF NOT EXISTS stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    batchno INT NOT NULL,
    arrival_date VARCHAR(10) NOT NULL,
    expiry_date VARCHAR(10) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed Categories
INSERT INTO categories (id, name) VALUES (1, 'Dairy & Eggs') ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO categories (id, name) VALUES (2, 'Bakery & Bread') ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO categories (id, name) VALUES (3, 'Beverages & Drinks') ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO categories (id, name) VALUES (4, 'Snacks & Sweets') ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO categories (id, name) VALUES (5, 'Canned & Packaged Foods') ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO categories (id, name) VALUES (6, 'Fresh Produce & Fruits') ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO categories (id, name) VALUES (7, 'Meat & Seafood') ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO categories (id, name) VALUES (8, 'Personal Care & Medicine') ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Seed Sample Products
INSERT INTO products (id, name, price, category_id) VALUES (1, 'Organic Whole Milk 1L', 65.0, 1) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (2, 'Greek Style Yogurt 500g', 85.0, 1) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (3, 'Cheddar Cheese Block 250g', 180.0, 1) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (4, 'Farm Fresh Large Eggs 12pk', 95.0, 1) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (5, 'Fresh Brioche Loaf', 55.0, 2) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (6, 'Butter Croissants 4-Pack', 120.0, 2) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (7, 'Whole Wheat Sliced Bread', 45.0, 2) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (8, 'Blueberry Muffins 4-Pack', 140.0, 2) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (9, 'Cold Brew Iced Coffee 330ml', 120.0, 3) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (10, 'Fresh Squeezed Orange Juice 1L', 110.0, 3) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (11, 'Sparkling Mineral Water 6x500ml', 150.0, 3) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (12, 'Green Tea Lemon 500ml', 50.0, 3) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (13, 'Dark Chocolate Almond Bar 100g', 99.0, 4) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (14, 'Sea Salt Kettle Chips 150g', 60.0, 4) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (15, 'Roasted Cashews 200g', 240.0, 4) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (16, 'Organic Chopped Tomatoes 400g', 75.0, 5) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (17, 'Canned Chickpeas in Water 400g', 80.0, 5) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (18, 'Premium Olive Oil 750ml', 650.0, 5) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (19, 'Fresh Strawberries 400g', 160.0, 6) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (20, 'Organic Baby Spinach 200g', 40.0, 6) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (21, 'Hass Avocados 4-Pack', 220.0, 6) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (22, 'Chicken Breast Fillets 500g', 190.0, 7) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (23, 'Smoked Salmon Slices 200g', 380.0, 7) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (24, 'Vitamin C 1000mg Effervescent 20s', 250.0, 8) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (25, 'Paracetamol Tablets 500mg 24pk', 35.0, 8) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);

-- Seed Sample Stock Batches
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (1, 1, 6, 101, '2026-09-05', '2026-09-18') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (2, 1, 24, 102, '2026-09-18', '2026-10-04') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (3, 2, 15, 103, '2026-09-10', '2026-09-24') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (4, 3, 18, 104, '2026-08-25', '2026-10-12') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (5, 4, 30, 105, '2026-09-15', '2026-10-15') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (6, 5, 8, 106, '2026-09-12', '2026-09-17') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (7, 6, 12, 107, '2026-09-18', '2026-09-23') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (8, 7, 20, 108, '2026-09-19', '2026-09-26') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (9, 8, 14, 109, '2026-09-16', '2026-09-22') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (10, 9, 16, 110, '2026-09-10', '2026-09-25') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (11, 10, 22, 111, '2026-09-14', '2026-10-06') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (12, 11, 40, 112, '2026-09-01', '2027-03-15') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (13, 12, 35, 113, '2026-08-20', '2027-01-20') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (14, 13, 50, 114, '2026-09-01', '2027-04-10') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (15, 14, 35, 115, '2026-09-05', '2026-12-15') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (16, 15, 28, 116, '2026-08-15', '2027-02-28') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (17, 16, 60, 117, '2026-08-01', '2027-09-30') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (18, 17, 45, 118, '2026-08-10', '2027-11-15') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (19, 18, 25, 119, '2026-07-20', '2027-08-20') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (20, 19, 10, 120, '2026-09-10', '2026-09-16') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (21, 20, 18, 121, '2026-09-17', '2026-09-22') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (22, 21, 20, 122, '2026-09-16', '2026-09-25') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (23, 22, 12, 123, '2026-09-17', '2026-09-23') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (24, 23, 15, 124, '2026-09-12', '2026-10-02') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (25, 24, 30, 125, '2026-08-15', '2027-06-30') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (26, 25, 50, 126, '2026-08-01', '2027-12-31') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
