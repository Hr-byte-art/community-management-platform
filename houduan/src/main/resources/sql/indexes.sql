-- 添加数据库索引以提高查询性能

-- 为常用查询字段添加索引
ALTER TABLE sys_user ADD INDEX idx_username (username);
ALTER TABLE sys_user ADD INDEX idx_phone (phone);
ALTER TABLE sys_user ADD INDEX idx_create_time (create_time);

-- 居民信息表索引
ALTER TABLE resident ADD INDEX idx_id_card (id_card);
ALTER TABLE resident ADD INDEX idx_phone (phone);
ALTER TABLE resident ADD INDEX idx_address (address);
ALTER TABLE resident ADD INDEX idx_building_unit_room (building_no, unit_no, room_no);
ALTER TABLE resident ADD INDEX idx_create_time (create_time);

-- 活动表索引
ALTER TABLE community_activity ADD INDEX idx_title (title);
ALTER TABLE community_activity ADD INDEX idx_activity_type (activity_type);
ALTER TABLE community_activity ADD INDEX idx_start_time (start_time);
ALTER TABLE community_activity ADD INDEX idx_create_time (create_time);

-- 活动报名表索引
ALTER TABLE activity_registration ADD INDEX idx_activity_user (activity_id, user_id);
ALTER TABLE activity_registration ADD INDEX idx_status (status);
ALTER TABLE activity_registration ADD INDEX idx_register_time (register_time);

-- 工单表索引
ALTER TABLE work_order ADD INDEX idx_title (title);
ALTER TABLE work_order ADD INDEX idx_order_type (order_type);
ALTER TABLE work_order ADD INDEX idx_status (status);
ALTER TABLE work_order ADD INDEX idx_submitter_handler (submitter_id, handler_id);
ALTER TABLE work_order ADD INDEX idx_create_time (create_time);

-- 流动人口表索引
ALTER TABLE floating_population ADD INDEX idx_name (name);
ALTER TABLE floating_population ADD INDEX idx_id_card (id_card);
ALTER TABLE floating_population ADD INDEX idx_phone (phone);
ALTER TABLE floating_population ADD INDEX idx_current_address (current_address);
ALTER TABLE floating_population ADD INDEX idx_expected_leave_date (expected_leave_date);
ALTER TABLE floating_population ADD INDEX idx_create_time (create_time);

-- 志愿者表索引
ALTER TABLE volunteer ADD INDEX idx_user_id (user_id);
ALTER TABLE volunteer ADD INDEX idx_phone (phone);
ALTER TABLE volunteer ADD INDEX idx_status (status);
ALTER TABLE volunteer ADD INDEX idx_join_date (join_date);

-- 治安隐患表索引
ALTER TABLE security_hazard ADD INDEX idx_title (title);
ALTER TABLE security_hazard ADD INDEX idx_hazard_type (hazard_type);
ALTER TABLE security_hazard ADD INDEX idx_location (location);
ALTER TABLE security_hazard ADD INDEX idx_status (status);
ALTER TABLE security_hazard ADD INDEX idx_reporter_handler (reporter_id, handler_id);
ALTER TABLE security_hazard ADD INDEX idx_create_time (create_time);

-- 通知公告表索引
ALTER TABLE notice ADD INDEX idx_title (title);
ALTER TABLE notice ADD INDEX idx_notice_type (notice_type);
ALTER TABLE notice ADD INDEX idx_is_top (is_top);
ALTER TABLE notice ADD INDEX idx_publish_time (publish_time);
ALTER TABLE notice ADD INDEX idx_publisher (publisher_id);

-- 通知阅读记录表索引
ALTER TABLE notice_read_record ADD INDEX idx_notice_user (notice_id, user_id);
ALTER TABLE notice_read_record ADD INDEX idx_read_time (read_time);

-- 邻里互助表索引
ALTER TABLE neighbor_help ADD INDEX idx_title (title);
ALTER TABLE neighbor_help ADD INDEX idx_help_type (help_type);
ALTER TABLE neighbor_help ADD INDEX idx_category (category);
ALTER TABLE neighbor_help ADD INDEX idx_status (status);
ALTER TABLE neighbor_help ADD INDEX idx_user_id (user_id);
ALTER TABLE neighbor_help ADD INDEX idx_create_time (create_time);

-- 服务预约表索引
ALTER TABLE service_appointment ADD INDEX idx_service_type (service_type);
ALTER TABLE service_appointment ADD INDEX idx_title (title);
ALTER TABLE service_appointment ADD INDEX idx_status (status);
ALTER TABLE service_appointment ADD INDEX idx_user_id (user_id);
ALTER TABLE service_appointment ADD INDEX idx_appointment_time (appointment_time);
ALTER TABLE service_appointment ADD INDEX idx_create_time (create_time);

-- 操作日志表索引
ALTER TABLE operation_log ADD INDEX idx_user_id (user_id);
ALTER TABLE operation_log ADD INDEX idx_username (username);
ALTER TABLE operation_log ADD INDEX idx_operation (operation);
ALTER TABLE operation_log ADD INDEX idx_create_time (create_time);

-- 家庭关系表索引
ALTER TABLE family_relation ADD INDEX idx_resident_related (resident_id, related_resident_id);
ALTER TABLE operation_log ADD INDEX idx_create_time (create_time);
-- 积分系统索引
ALTER TABLE points_account ADD INDEX idx_total_points (total_points);
ALTER TABLE points_record ADD INDEX idx_user_time (user_id, create_time);
ALTER TABLE points_record ADD INDEX idx_business (business_type, business_id);

-- 消息中心索引
ALTER TABLE message_notice ADD INDEX idx_user_read_time (user_id, is_read, create_time);
ALTER TABLE message_notice ADD INDEX idx_business (business_type, business_id);
