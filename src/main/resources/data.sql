-- ======================================
-- SAMPLE ORDERS
-- ======================================
INSERT INTO orders (customer_id, order_status, order_total)
VALUES
    (101, 'PENDING', 450.75),
    (102, 'PROCESSING', 1200.00),
    (103, 'SHIPPED', 799.99),
    (101, 'PENDING', 300.50),
    (105, 'DELIVERED', 1500.00);

-- ======================================
-- SAMPLE ORDER ITEMS
-- ======================================
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
VALUES

    (1, 501, 2, 150.25),
    (1, 502, 1, 150.25),

    (2, 503, 3, 400.00),

    (3, 504, 1, 799.99),

    (4, 505, 2, 120.25),

    (5, 506, 1, 1500.00);

-- ======================================
-- SAMPLE AUDIT LOGS
-- ======================================
INSERT INTO audit_logs (table_name, previous_data, current_data)
VALUES
    ('orders', '{"status":"PENDING"}', '{"status":"PROCESSING"}'),
    ('orders', '{"status":"PROCESSING"}', '{"status":"SHIPPED"}');
