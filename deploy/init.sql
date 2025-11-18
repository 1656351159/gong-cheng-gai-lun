-- Schema initialization for smart_materials

-- 用户表需最先创建，后续表有外键引用
CREATE TABLE IF NOT EXISTS sys_user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
    email VARCHAR(128),
    phone VARCHAR(32),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS material_category (
    id SERIAL PRIMARY KEY,
    parent_id INTEGER REFERENCES material_category(id),
    name VARCHAR(128) NOT NULL,
    safe_stock INTEGER DEFAULT 0,
    unit VARCHAR(32),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS material_item (
    id SERIAL PRIMARY KEY,
    category_id INTEGER REFERENCES material_category(id),
    brand VARCHAR(128),
    model VARCHAR(128),
    spec VARCHAR(256),
    unit_price NUMERIC(12,2),
    currency VARCHAR(8) DEFAULT 'CNY',
    extra JSONB,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS stock_batch (
    id SERIAL PRIMARY KEY,
    item_id INTEGER REFERENCES material_item(id),
    batch_code VARCHAR(64) UNIQUE NOT NULL,
    quantity INTEGER NOT NULL,
    expire_date DATE,
    barcode VARCHAR(128),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS stock_transaction (
    id SERIAL PRIMARY KEY,
    batch_id INTEGER REFERENCES stock_batch(id),
    txn_type VARCHAR(16) NOT NULL CHECK (txn_type IN ('IN','OUT','RETURN','SCRAP')),
    qty INTEGER NOT NULL,
    user_id INTEGER REFERENCES sys_user(id),
    project_no VARCHAR(64),
    usage TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS request (
    id SERIAL PRIMARY KEY,
    item_id INTEGER REFERENCES material_item(id),
    qty INTEGER NOT NULL,
    purpose TEXT,
    project_no VARCHAR(64),
    status VARCHAR(16) NOT NULL,
    comment TEXT,
    student_id INTEGER REFERENCES sys_user(id),
    reviewer_id INTEGER REFERENCES sys_user(id),
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Seed demo data
-- BCrypt('123456') 代替明文，便于切换安全密码编码器
INSERT INTO sys_user (username, password_hash, role, email) VALUES
('admin', '$2a$10$Dow1ZH0iQWmvMRGsiE9zPuFMvx6bMpiKFFitvolSF/G5hgbf28pG6', 'admin', 'admin@example.com'),
('lab',   '$2a$10$Dow1ZH0iQWmvMRGsiE9zPuFMvx6bMpiKFFitvolSF/G5hgbf28pG6', 'lab', 'lab@example.com'),
('stu',   '$2a$10$Dow1ZH0iQWmvMRGsiE9zPuFMvx6bMpiKFFitvolSF/G5hgbf28pG6', 'student', 'stu@example.com');

INSERT INTO material_category (name, safe_stock, unit) VALUES
('Chem Reagent', 10, '瓶'),
('Electronics', 5, '个'),
('Lab Consumable', 20, '包');

INSERT INTO material_item (category_id, brand, model, spec, unit_price, currency, extra) VALUES
(1, 'Merck', 'HCl', '37% 500ml', 120.00, 'CNY', '{}'::jsonb),
(1, 'Sinopharm', 'NaOH', '98% 500g', 45.00, 'CNY', '{}'::jsonb),
(2, 'Keysight', 'Multimeter', 'U1253B', 2200.00, 'CNY', '{}'::jsonb),
(2, 'RaspberryPi', 'Pi4B', '4GB RAM', 380.00, 'CNY', '{}'::jsonb),
(3, '3M', 'Nitrile Gloves', 'M size 100pcs', 85.00, 'CNY', '{}'::jsonb);

-- Seed stock batch
INSERT INTO stock_batch (item_id, batch_code, quantity, expire_date, barcode) VALUES
(1, 'BATCH-HCL-001', 30, '2026-12-31', 'BAR-HCL-001'),
(1, 'BATCH-HCL-002', 20, '2027-06-30', 'BAR-HCL-002'),
(2, 'BATCH-NAOH-001', 50, '2026-01-31', 'BAR-NAOH-001'),
(3, 'BATCH-DMM-001', 5, NULL, 'BAR-DMM-001'),
(4, 'BATCH-RPI-001', 10, NULL, 'BAR-RPI-001'),
(5, 'BATCH-GLOVE-001', 100, '2025-12-31', 'BAR-GLOVE-001');

-- Seed requests demo
INSERT INTO request (item_id, qty, purpose, project_no, status, comment, student_id, reviewer_id, reviewed_at) VALUES
(1, 5, '酸洗实验', 'PRJ-001', 'approved', '已发放', (SELECT id FROM sys_user WHERE username='stu'), (SELECT id FROM sys_user WHERE username='lab'), NOW()),
(2, 10, '滴定实验', 'PRJ-002', 'pending', NULL, (SELECT id FROM sys_user WHERE username='stu'), NULL, NULL),
(3, 1, '仪表校准', 'PRJ-003', 'rejected', '库存预留给项目A', (SELECT id FROM sys_user WHERE username='stu'), (SELECT id FROM sys_user WHERE username='admin'), NOW());
