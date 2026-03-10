-- Sprint-1: 消息中心（增量脚本）

CREATE TABLE IF NOT EXISTS message_notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    title VARCHAR(100) NOT NULL COMMENT '消息标题',
    content VARCHAR(1000) COMMENT '消息内容',
    message_type VARCHAR(30) NOT NULL COMMENT '消息类型',
    business_type VARCHAR(50) COMMENT '业务类型',
    business_id BIGINT COMMENT '业务ID',
    is_read TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    read_time DATETIME COMMENT '已读时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_user_read_time (user_id, is_read, create_time),
    KEY idx_business (business_type, business_id)
) COMMENT '站内消息表';
