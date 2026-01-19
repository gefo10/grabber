-- 1. Insert Categories (Safe for Rerun)
INSERT INTO categories (category_id, category_name) 
VALUES (1, 'Electronics')
ON CONFLICT (category_id) DO NOTHING;

INSERT INTO categories (category_id, category_name) 
VALUES (2, 'Home & Office')
ON CONFLICT (category_id) DO NOTHING;

-- 2. Insert Products (Safe for Rerun)
-- Note: We now link them to Category 1 (Electronics) instead of NULL
INSERT INTO products (product_id, product_name, description, price, discount, special_price, image, quantity, version, category_id)
VALUES (101, 'Mechanical Keyboard', 'Clicky and loud.', 120.00, 0.0, 120.00, 'https://placehold.co/600x400', 50, 0, 1)
ON CONFLICT (product_id) DO NOTHING;

INSERT INTO products (product_id, product_name, description, price, discount, special_price, image, quantity, version, category_id)
VALUES (102, 'Gaming Mouse', 'High DPI for pros.', 59.99, 10.0, 49.99, 'https://placehold.co/600x400', 100, 0, 1)
ON CONFLICT (product_id) DO NOTHING;

INSERT INTO products (product_id, product_name, description, price, discount, special_price, image, quantity, version, category_id)
VALUES (103, '4K Monitor', 'Crystal clear pixels.', 300.50, 0.0, 300.50, 'https://placehold.co/600x400', 20, 0, 1)
ON CONFLICT (product_id) DO NOTHING;

INSERT INTO products (product_id, product_name, description, price, discount, special_price, image, quantity, version, category_id)
VALUES (104, 'USB-C Cable', 'Fast charging.', 15.00, 0.0, 15.00, 'https://placehold.co/600x400', 200, 0, 1)
ON CONFLICT (product_id) DO NOTHING;
