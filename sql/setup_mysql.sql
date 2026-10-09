-- MySQL Schema and Seed Data for Expiry Finder
CREATE DATABASE IF NOT EXISTS expiry_finder;
USE expiry_finder;

-- 1. Categories Table (Exact Schema)
CREATE TABLE IF NOT EXISTS categories (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          name VARCHAR(255) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Products Table (Exact Schema)
CREATE TABLE IF NOT EXISTS products (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL,
    category_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Stock Batches Table (Exact Schema: batchno INT, dates VARCHAR(10))
CREATE TABLE IF NOT EXISTS stock (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     product_id BIGINT NOT NULL,
                                     quantity INT NOT NULL,
                                     batchno INT NOT NULL,
                                     arrival_date VARCHAR(10) NOT NULL,
    expiry_date VARCHAR(10) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- Seed Categories
-- ============================================================================
INSERT INTO categories (id, name) VALUES (1, 'Dairy & Eggs') ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO categories (id, name) VALUES (2, 'Bakery & Bread') ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO categories (id, name) VALUES (3, 'Beverages & Drinks') ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO categories (id, name) VALUES (4, 'Snacks & Sweets') ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO categories (id, name) VALUES (5, 'Canned & Packaged Foods') ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO categories (id, name) VALUES (6, 'Fresh Produce & Fruits') ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO categories (id, name) VALUES (7, 'Meat & Seafood') ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO categories (id, name) VALUES (8, 'Personal Care & Medicine') ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO categories (id, name) VALUES (9, 'Frozen Foods') ON DUPLICATE KEY UPDATE name=VALUES(name);
INSERT INTO categories (id, name) VALUES (10, 'Condiments & Sauces') ON DUPLICATE KEY UPDATE name=VALUES(name);

-- ============================================================================
-- Seed Sample Products
-- ============================================================================
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
INSERT INTO products (id, name, price, category_id) VALUES (16, 'Artisan Oat Cookies 250g', 115.0, 4) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (17, 'Organic Chopped Tomatoes 400g', 75.0, 5) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (18, 'Canned Chickpeas in Water 400g', 80.0, 5) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (19, 'Premium Olive Oil 750ml', 650.0, 5) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (20, 'Dried Penne Pasta 500g', 90.0, 5) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (21, 'Fresh Strawberries 400g', 160.0, 6) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (22, 'Organic Baby Spinach 200g', 40.0, 6) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (23, 'Hass Avocados 4-Pack', 220.0, 6) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (24, 'Button Mushrooms 250g', 70.0, 6) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (25, 'Chicken Breast Fillets 500g', 190.0, 7) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (26, 'Smoked Salmon Slices 200g', 380.0, 7) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (27, 'Minced Beef 500g', 260.0, 7) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (28, 'Vitamin C 1000mg Effervescent 20s', 250.0, 8) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (29, 'Paracetamol Tablets 500mg 24pk', 35.0, 8) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (30, 'Saline Nasal Spray 50ml', 145.0, 8) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (31, 'Frozen Garden Peas 1kg', 130.0, 9) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);
INSERT INTO products (id, name, price, category_id) VALUES (32, 'Artisan Basil Pesto 190g', 210.0, 10) ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), category_id=VALUES(category_id);

-- ============================================================================
-- Seed Sample Stock Batches (All batchno are pure INTs)
-- ============================================================================
-- 1. Expired Batches (Write-offs / Purge alerts)
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (1, 1, 6, 101, '2026-09-10', '2026-09-25') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (2, 5, 4, 102, '2026-09-15', '2026-09-22') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (3, 7, 8, 103, '2026-09-18', '2026-09-27') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (4, 8, 5, 104, '2026-09-20', '2026-09-29') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (5, 21, 6, 105, '2026-09-22', '2026-09-30') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (6, 22, 10, 106, '2026-09-24', '2026-10-02') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (7, 25, 4, 107, '2026-09-25', '2026-10-04') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (8, 27, 3, 108, '2026-09-26', '2026-10-05') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);

-- 2. Urgent / Near Expiry (Expiring within 1-5 days)
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (9, 1, 14, 109, '2026-09-28', '2026-10-10') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (10, 2, 8, 110, '2026-09-25', '2026-10-11') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (11, 6, 12, 111, '2026-10-02', '2026-10-11') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (12, 10, 10, 112, '2026-09-29', '2026-10-12') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (13, 23, 15, 113, '2026-10-01', '2026-10-12') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (14, 24, 18, 114, '2026-10-03', '2026-10-13') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (15, 25, 12, 115, '2026-10-04', '2026-10-14') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);

-- 3. Approaching Expiry (6-15 days)
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (16, 1, 25, 116, '2026-10-02', '2026-10-16') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (17, 4, 35, 117, '2026-09-20', '2026-10-17') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (18, 7, 20, 118, '2026-10-04', '2026-10-17') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (19, 9, 16, 119, '2026-09-28', '2026-10-18') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (20, 26, 10, 120, '2026-09-27', '2026-10-19') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (21, 3, 22, 121, '2026-09-12', '2026-10-21') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (22, 5, 12, 122, '2026-10-05', '2026-10-22') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);

-- 4. Moderate Shelf-Life (2-6 Weeks)
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (23, 2, 30, 123, '2026-10-01', '2026-10-28') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (24, 4, 45, 124, '2026-10-02', '2026-11-02') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (25, 10, 20, 125, '2026-10-04', '2026-11-05') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (26, 14, 40, 126, '2026-09-15', '2026-11-15') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (27, 16, 25, 127, '2026-09-20', '2026-11-20') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (28, 32, 18, 128, '2026-09-25', '2026-11-30') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);

-- 5. Long Shelf-Life (3-8 Months)
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (29, 11, 55, 129, '2026-09-10', '2027-02-15') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (30, 12, 40, 130, '2026-09-12', '2027-01-20') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (31, 13, 60, 131, '2026-09-25', '2027-03-10') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (32, 15, 30, 132, '2026-09-01', '2027-03-01') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (33, 20, 70, 133, '2026-08-15', '2027-08-15') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (34, 31, 45, 134, '2026-09-10', '2027-05-10') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);

-- 6. Extended Shelf-Life (1-2 Years)
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (35, 17, 85, 135, '2026-08-10', '2028-02-15') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (36, 18, 60, 136, '2026-08-20', '2028-04-30') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (37, 19, 35, 137, '2026-07-25', '2028-07-25') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (38, 28, 50, 138, '2026-09-01', '2028-06-30') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (39, 29, 95, 139, '2026-09-01', '2028-12-31') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (40, 30, 40, 140, '2026-08-15', '2028-08-15') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);

-- 7. Secondary Batches for Same Products (Testing FEFO / Multi-Batch Ordering)
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (41, 1, 30, 141, '2026-10-06', '2026-10-24') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (42, 1, 40, 142, '2026-10-08', '2026-10-30') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (43, 2, 35, 143, '2026-10-06', '2026-11-12') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (44, 3, 28, 144, '2026-10-02', '2026-11-28') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (45, 4, 50, 145, '2026-10-07', '2026-11-20') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (46, 6, 20, 146, '2026-10-08', '2026-10-18') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (47, 7, 30, 147, '2026-10-07', '2026-10-22') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (48, 8, 22, 148, '2026-10-06', '2026-10-16') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (49, 9, 25, 149, '2026-10-05', '2026-10-30') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (50, 10, 30, 150, '2026-10-07', '2026-11-18') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (51, 14, 50, 151, '2026-10-01', '2027-01-10') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (52, 15, 40, 152, '2026-09-20', '2027-04-20') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (53, 21, 15, 153, '2026-10-07', '2026-10-14') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (54, 22, 25, 154, '2026-10-07', '2026-10-15') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (55, 23, 20, 155, '2026-10-06', '2026-10-21') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (56, 24, 30, 156, '2026-10-08', '2026-10-20') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (57, 25, 25, 157, '2026-10-08', '2026-10-22') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (58, 26, 18, 158, '2026-10-05', '2026-10-28') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (59, 27, 22, 159, '2026-10-07', '2026-10-23') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);
INSERT INTO stock (id, product_id, quantity, batchno, arrival_date, expiry_date) VALUES (60, 31, 35, 160, '2026-10-01', '2027-06-15') ON DUPLICATE KEY UPDATE quantity=VALUES(quantity), batchno=VALUES(batchno), arrival_date=VALUES(arrival_date), expiry_date=VALUES(expiry_date);