-- Sprint-1: 积分系统（增量脚本）

CREATE TABLE IF NOT EXISTS points_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_points INT NOT NULL DEFAULT 0 COMMENT '总积分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_id (user_id)
) COMMENT '积分账户表';

CREATE TABLE IF NOT EXISTS points_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    change_type TINYINT NOT NULL COMMENT '变动类型：1-增加，-1-扣减',
    points INT NOT NULL COMMENT '变动积分值(正数)',
    business_type VARCHAR(50) NOT NULL COMMENT '业务类型',
    business_id BIGINT NOT NULL COMMENT '业务ID',
    remark VARCHAR(255) COMMENT '备注',
    operator_id BIGINT COMMENT '操作人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_unique_event (user_id, business_type, business_id, change_type),
    KEY idx_user_time (user_id, create_time),
    KEY idx_business (business_type, business_id)
) COMMENT '积分流水表';
