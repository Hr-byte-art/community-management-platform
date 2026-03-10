-- Sprint-3 P1: 满意度评价闭环 + 网格化自动派单

CREATE TABLE IF NOT EXISTS service_evaluation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    work_order_id BIGINT NOT NULL COMMENT '工单ID',
    user_id BIGINT NOT NULL COMMENT '评价用户ID（工单提交人）',
    score TINYINT NOT NULL COMMENT '评分(1-5)',
    content VARCHAR(500) DEFAULT '' COMMENT '评价内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_work_order_user (work_order_id, user_id),
    KEY idx_eval_user (user_id),
    KEY idx_eval_score (score)
) COMMENT='工单服务评价表';

CREATE TABLE IF NOT EXISTS grid_dispatch_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    grid_name VARCHAR(100) DEFAULT NULL COMMENT '网格名称/规则名称',
    order_type VARCHAR(20) DEFAULT NULL COMMENT '工单类型，NULL表示通用',
    priority TINYINT DEFAULT NULL COMMENT '优先级，NULL表示通用',
    assignee_id BIGINT NOT NULL COMMENT '自动派单责任人ID',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0-否，1-是',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_dispatch_order_type_priority (order_type, priority),
    KEY idx_dispatch_enabled (enabled),
    KEY idx_dispatch_assignee (assignee_id)
) COMMENT='网格化自动派单规则表';

INSERT INTO grid_dispatch_rule (grid_name, order_type, priority, assignee_id, enabled, remark)
SELECT '默认报修派单规则', 'REPAIR', NULL, 1, 1, '优先派给管理员ID=1'
WHERE NOT EXISTS (
    SELECT 1 FROM grid_dispatch_rule WHERE order_type = 'REPAIR' AND priority IS NULL
);
