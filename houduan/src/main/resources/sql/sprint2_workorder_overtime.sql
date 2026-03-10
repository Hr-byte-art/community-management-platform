-- Sprint-2: 工单超时治理（增量脚本）
-- 兼容 MySQL 5.7/8.0：通过 information_schema 判断是否存在，再动态执行 DDL。

-- 1) 字段：deadline
SET @ddl = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'work_order'
              AND COLUMN_NAME = 'deadline'
        ),
        'SELECT ''column deadline already exists''',
        'ALTER TABLE work_order ADD COLUMN deadline DATETIME COMMENT ''要求完成时间'' AFTER priority'
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2) 字段：assignee_id
SET @ddl = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'work_order'
              AND COLUMN_NAME = 'assignee_id'
        ),
        'SELECT ''column assignee_id already exists''',
        'ALTER TABLE work_order ADD COLUMN assignee_id BIGINT COMMENT ''责任人ID'' AFTER handler_id'
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3) 字段：is_overtime
SET @ddl = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'work_order'
              AND COLUMN_NAME = 'is_overtime'
        ),
        'SELECT ''column is_overtime already exists''',
        'ALTER TABLE work_order ADD COLUMN is_overtime TINYINT NOT NULL DEFAULT 0 COMMENT ''是否超时：0-否，1-是'' AFTER deadline'
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 4) 索引：deadline
SET @ddl = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM information_schema.STATISTICS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'work_order'
              AND INDEX_NAME = 'idx_work_order_deadline'
        ),
        'SELECT ''index idx_work_order_deadline already exists''',
        'CREATE INDEX idx_work_order_deadline ON work_order (deadline)'
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 5) 索引：assignee_id
SET @ddl = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM information_schema.STATISTICS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'work_order'
              AND INDEX_NAME = 'idx_work_order_assignee'
        ),
        'SELECT ''index idx_work_order_assignee already exists''',
        'CREATE INDEX idx_work_order_assignee ON work_order (assignee_id)'
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 6) 复合索引：is_overtime + status
SET @ddl = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM information_schema.STATISTICS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'work_order'
              AND INDEX_NAME = 'idx_work_order_overtime_status'
        ),
        'SELECT ''index idx_work_order_overtime_status already exists''',
        'CREATE INDEX idx_work_order_overtime_status ON work_order (is_overtime, status)'
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
