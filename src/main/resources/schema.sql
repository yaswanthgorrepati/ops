-- ============================
-- ORDERS TABLE
-- ============================
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    order_status VARCHAR(50) NOT NULL,
    order_total DOUBLE NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_order_status ON orders(order_status);
CREATE INDEX idx_orders_customer_status ON orders(customer_id, order_status);


-- ============================
-- ORDER ITEMS TABLE
-- ============================
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity SMALLINT NOT NULL,
    unit_price DOUBLE NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
CREATE INDEX idx_order_items_order_product ON order_items(order_id, product_id);


-- ============================
-- CRON JOB LOGS TABLE
-- ============================
CREATE TABLE IF NOT EXISTS cron_job_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_name VARCHAR(255) NOT NULL,
    total_orders_count INT,
    success_count INT,
    failure_count INT,
    start_index BIGINT,
    end_index BIGINT,

    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_cron_job_logs_job_name ON cron_job_logs(job_name);
CREATE INDEX idx_cron_job_logs_start_index ON cron_job_logs(start_index);
CREATE INDEX idx_cron_job_logs_end_index ON cron_job_logs(end_index);
CREATE INDEX idx_cron_job_logs_job_start_end ON cron_job_logs(job_name, start_index, end_index);

-- ============================
-- AUDIT LOGS TABLE
-- ============================
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    table_name VARCHAR(255) NOT NULL,
    previous_data TEXT,
    current_data TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
CREATE INDEX idx_audit_logs_table_name ON audit_logs(table_name);
CREATE INDEX idx_audit_logs_table_created ON audit_logs(table_name, created_at);

-- ============================
-- CRON JOB BATCH RETRY TABLE
-- ============================
CREATE TABLE IF NOT EXISTS cron_job_batch_retry (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    start_index BIGINT,
    end_index BIGINT,
    job_name VARCHAR(255) NOT NULL,
    retry_count INT NOT NULL DEFAULT 1,
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_cron_job_batch_retry_job_name ON cron_job_batch_retry(job_name);
CREATE INDEX idx_cron_job_batch_retry_retry_count ON cron_job_batch_retry(retry_count);
CREATE INDEX idx_cron_job_batch_retry_job_retry ON cron_job_batch_retry(job_name, retry_count);

