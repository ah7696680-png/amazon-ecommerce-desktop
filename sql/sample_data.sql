-- Insert sample data for testing

-- Passwords are hashed with BCrypt (password: "Password@123" for all)
-- Admin: admin@ecommerce.com
-- Seller: seller@ecommerce.com
-- Customer: customer@ecommerce.com

INSERT INTO users (username, email, password_hash, first_name, last_name, phone, role, is_active, is_verified) VALUES
('admin_user', 'admin@ecommerce.com', '$2a$12$..hash_here..', 'Admin', 'User', '+1-800-000-0001', 'ADMIN', TRUE, TRUE),
('seller_user', 'seller@ecommerce.com', '$2a$12$..hash_here..', 'John', 'Seller', '+1-800-000-0002', 'SELLER', TRUE, TRUE),
('customer_user', 'customer@ecommerce.com', '$2a$12$..hash_here..', 'Jane', 'Customer', '+1-800-000-0003', 'CUSTOMER', TRUE, TRUE);

-- Insert categories
INSERT INTO categories (name, slug, description, display_order, is_active) VALUES
('Clothes', 'clothes', 'Clothing and apparel', 1, TRUE),
('Shoes', 'shoes', 'Footwear and shoes', 2, TRUE),
('Electronics', 'electronics', 'Electronic devices and gadgets', 3, TRUE),
('Watches', 'watches', 'Timepieces and watches', 4, TRUE),
('Perfumes', 'perfumes', 'Fragrances and perfumes', 5, TRUE),
('Accessories', 'accessories', 'Bags, belts, and accessories', 6, TRUE),
('Home Products', 'home-products', 'Home and living products', 7, TRUE);

-- Insert seller profile
INSERT INTO seller_profiles (user_id, shop_name, business_license, is_verified, rating) VALUES
(2, 'Premium Store', 'LICENSE123456', TRUE, 4.5);

-- Insert sample products (Clothes)
INSERT INTO products (seller_id, category_id, name, slug, description, price, cost_price, discount_percent, sku, quantity_in_stock, main_image_url, status, is_featured, is_verified, rating, review_count) VALUES
(1, 1, 'Blue Cotton T-Shirt', 'blue-cotton-tshirt', 'High-quality blue cotton t-shirt', 29.99, 10.00, 10, 'TSHIRT001', 150, 'https://via.placeholder.com/300?text=Blue+T-Shirt', 'ACTIVE', TRUE, TRUE, 4.3, 25),
(1, 1, 'Black Denim Jeans', 'black-denim-jeans', 'Classic black denim jeans', 59.99, 20.00, 15, 'JEANS001', 100, 'https://via.placeholder.com/300?text=Black+Jeans', 'ACTIVE', TRUE, TRUE, 4.5, 45),
(1, 1, 'Red Sweater', 'red-sweater', 'Cozy red wool sweater', 49.99, 18.00, 20, 'SWEATER001', 80, 'https://via.placeholder.com/300?text=Red+Sweater', 'ACTIVE', FALSE, TRUE, 4.2, 15);

-- Insert sample products (Shoes)
INSERT INTO products (seller_id, category_id, name, slug, description, price, cost_price, discount_percent, sku, quantity_in_stock, main_image_url, status, is_featured, is_verified, rating, review_count) VALUES
(1, 2, 'White Sneakers', 'white-sneakers', 'Comfortable white sneakers', 79.99, 30.00, 5, 'SHOE001', 200, 'https://via.placeholder.com/300?text=White+Sneakers', 'ACTIVE', TRUE, TRUE, 4.6, 60),
(1, 2, 'Black Formal Shoes', 'black-formal-shoes', 'Professional black formal shoes', 119.99, 45.00, 10, 'SHOE002', 75, 'https://via.placeholder.com/300?text=Formal+Shoes', 'ACTIVE', FALSE, TRUE, 4.4, 30),
(1, 2, 'Running Shoes', 'running-shoes', 'High-performance running shoes', 99.99, 40.00, 15, 'SHOE003', 120, 'https://via.placeholder.com/300?text=Running+Shoes', 'ACTIVE', TRUE, TRUE, 4.7, 50);

-- Insert sample products (Electronics)
INSERT INTO products (seller_id, category_id, name, slug, description, price, cost_price, discount_percent, sku, quantity_in_stock, main_image_url, status, is_featured, is_verified, rating, review_count) VALUES
(1, 3, 'Wireless Headphones', 'wireless-headphones', 'Premium wireless noise-canceling headphones', 199.99, 80.00, 5, 'AUDIO001', 50, 'https://via.placeholder.com/300?text=Headphones', 'ACTIVE', TRUE, TRUE, 4.8, 120),
(1, 3, 'USB-C Power Bank', 'usb-c-power-bank', '20000mAh portable power bank', 49.99, 15.00, 10, 'POWER001', 300, 'https://via.placeholder.com/300?text=Power+Bank', 'ACTIVE', TRUE, TRUE, 4.5, 85),
(1, 3, 'Phone Screen Protector', 'phone-screen-protector', 'Tempered glass screen protector', 14.99, 3.00, 20, 'SCREEN001', 500, 'https://via.placeholder.com/300?text=Screen+Protector', 'ACTIVE', FALSE, TRUE, 4.4, 40);

-- Insert sample products (Watches)
INSERT INTO products (seller_id, category_id, name, slug, description, price, cost_price, discount_percent, sku, quantity_in_stock, main_image_url, status, is_featured, is_verified, rating, review_count) VALUES
(1, 4, 'Luxury Analog Watch', 'luxury-analog-watch', 'Premium stainless steel analog watch', 299.99, 100.00, 0, 'WATCH001', 40, 'https://via.placeholder.com/300?text=Luxury+Watch', 'ACTIVE', TRUE, TRUE, 4.9, 95),
(1, 4, 'Digital Sports Watch', 'digital-sports-watch', 'Waterproof digital sports watch', 89.99, 30.00, 10, 'WATCH002', 100, 'https://via.placeholder.com/300?text=Sports+Watch', 'ACTIVE', FALSE, TRUE, 4.6, 55);

-- Insert sample products (Perfumes)
INSERT INTO products (seller_id, category_id, name, slug, description, price, cost_price, discount_percent, sku, quantity_in_stock, main_image_url, status, is_featured, is_verified, rating, review_count) VALUES
(1, 5, 'Designer Perfume', 'designer-perfume', 'Premium designer fragrance 100ml', 149.99, 50.00, 5, 'PERF001', 75, 'https://via.placeholder.com/300?text=Perfume', 'ACTIVE', TRUE, TRUE, 4.7, 70),
(1, 5, 'Men Cologne', 'men-cologne', 'Fresh men cologne spray 75ml', 79.99, 25.00, 10, 'PERF002', 120, 'https://via.placeholder.com/300?text=Cologne', 'ACTIVE', FALSE, TRUE, 4.5, 45);

-- Insert sample products (Accessories)
INSERT INTO products (seller_id, category_id, name, slug, description, price, cost_price, discount_percent, sku, quantity_in_stock, main_image_url, status, is_featured, is_verified, rating, review_count) VALUES
(1, 6, 'Leather Backpack', 'leather-backpack', 'Premium leather laptop backpack', 129.99, 45.00, 8, 'BAG001', 60, 'https://via.placeholder.com/300?text=Backpack', 'ACTIVE', TRUE, TRUE, 4.6, 40),
(1, 6, 'Sunglasses', 'sunglasses', 'UV protection sunglasses', 69.99, 20.00, 15, 'GLASS001', 200, 'https://via.placeholder.com/300?text=Sunglasses', 'ACTIVE', TRUE, TRUE, 4.4, 30),
(1, 6, 'Leather Belt', 'leather-belt', 'Classic leather dress belt', 49.99, 15.00, 10, 'BELT001', 150, 'https://via.placeholder.com/300?text=Belt', 'ACTIVE', FALSE, TRUE, 4.3, 20);

-- Insert sample products (Home)
INSERT INTO products (seller_id, category_id, name, slug, description, price, cost_price, discount_percent, sku, quantity_in_stock, main_image_url, status, is_featured, is_verified, rating, review_count) VALUES
(1, 7, 'LED Table Lamp', 'led-table-lamp', 'Modern LED table lamp with USB charging', 39.99, 12.00, 20, 'HOME001', 200, 'https://via.placeholder.com/300?text=Table+Lamp', 'ACTIVE', TRUE, TRUE, 4.5, 35),
(1, 7, 'Coffee Maker', 'coffee-maker', 'Automatic drip coffee maker', 79.99, 28.00, 10, 'HOME002', 80, 'https://via.placeholder.com/300?text=Coffee+Maker', 'ACTIVE', FALSE, TRUE, 4.6, 50),
(1, 7, 'Desk Organizer', 'desk-organizer', 'Bamboo desk organizer set', 24.99, 8.00, 15, 'HOME003', 300, 'https://via.placeholder.com/300?text=Desk+Organizer', 'ACTIVE', FALSE, TRUE, 4.3, 15);

-- Insert sample reviews
INSERT INTO reviews (product_id, user_id, order_id, rating, title, comment, is_verified_purchase, status) VALUES
(1, 3, NULL, 5, 'Great quality!', 'Excellent t-shirt, very comfortable and soft cotton', TRUE, 'APPROVED'),
(1, 3, NULL, 4, 'Good value', 'Nice color, fits perfectly', TRUE, 'APPROVED'),
(2, 3, NULL, 5, 'Perfect jeans', 'Classic fit, durable material', TRUE, 'APPROVED'),
(4, 3, NULL, 5, 'Highly recommended', 'Very comfortable sneakers, great for daily wear', TRUE, 'APPROVED'),
(6, 3, NULL, 5, 'Best headphones', 'Crystal clear sound, excellent noise cancellation', TRUE, 'APPROVED');

-- Insert sample coupons
INSERT INTO coupons (code, description, discount_type, discount_value, min_purchase_amount, usage_limit, is_active, valid_from, valid_until) VALUES
('WELCOME10', 'Welcome discount', 'PERCENTAGE', 10, 50, 100, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY)),
('SAVE20', '20% off on all items', 'PERCENTAGE', 20, 100, 50, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 15 DAY)),
('FLAT10', 'Flat $10 off', 'FIXED_AMOUNT', 10, 30, 200, TRUE, NOW(), DATE_ADD(NOW(), INTERVAL 45 DAY));

-- Insert sample cart for customer
INSERT INTO cart (user_id, total_items, subtotal) VALUES (3, 0, 0.00);
