/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80012
 Source Host           : localhost:3306
 Source Schema         : community_service

 Target Server Type    : MySQL
 Target Server Version : 80012
 File Encoding         : 65001

 Date: 19/03/2026 20:15:55
*/

SET NAMES utf8mb4;
DROP DATABASE IF EXISTS `community_service`;
CREATE DATABASE `community_service` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `community_service`;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity_registration
-- ----------------------------
DROP TABLE IF EXISTS `activity_registration`;
CREATE TABLE `activity_registration`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '状态：0-已报名，1-已签到，2-已取消',
  `register_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '报名时间',
  `check_in_time` datetime(0) NULL DEFAULT NULL COMMENT '签到时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动报名表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for community_activity
-- ----------------------------
DROP TABLE IF EXISTS `community_activity`;
CREATE TABLE `community_activity`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '活动内容',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'https://tu.ltyuanfang.cn/api/fengjing.php' COMMENT '封面图片',
  `activity_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动类型：CULTURE-文化，SPORT-体育，VOLUNTEER-志愿，OTHER-其他',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动地点',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
  `is_cancelled` tinyint(4) NULL DEFAULT 0 COMMENT '是否取消：0-正常，1-已取消',
  `organizer_id` bigint(20) NULL DEFAULT NULL COMMENT '组织者ID',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社区活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for family_relation
-- ----------------------------
DROP TABLE IF EXISTS `family_relation`;
CREATE TABLE `family_relation`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `resident_id` bigint(20) NOT NULL COMMENT '居民ID',
  `related_resident_id` bigint(20) NOT NULL COMMENT '关联居民ID',
  `relation` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关系：SPOUSE-配偶，PARENT-父母，CHILD-子女，SIBLING-兄弟姐妹',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '家庭关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for floating_population
-- ----------------------------
DROP TABLE IF EXISTS `floating_population`;
CREATE TABLE `floating_population`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `gender` tinyint(4) NULL DEFAULT NULL COMMENT '性别：0-女，1-男',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `origin_place` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '户籍所在地',
  `current_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '现居住地址',
  `work_unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工作单位',
  `register_date` date NULL DEFAULT NULL COMMENT '登记日期',
  `expected_leave_date` date NULL DEFAULT NULL COMMENT '预计离开日期',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-已离开，1-在住',
  `photo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'https://tu.ltyuanfang.cn/api/fengjing.php' COMMENT '照片',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '流动人口表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for grid_dispatch_rule
-- ----------------------------
DROP TABLE IF EXISTS `grid_dispatch_rule`;
CREATE TABLE `grid_dispatch_rule`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `grid_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '网格名称/规则名称',
  `order_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工单类型，NULL表示通用',
  `priority` tinyint(4) NULL DEFAULT NULL COMMENT '优先级，NULL表示通用',
  `assignee_id` bigint(20) NOT NULL COMMENT '自动派单责任人ID',
  `enabled` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否启用：0-否，1-是',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dispatch_order_type_priority`(`order_type`, `priority`) USING BTREE,
  INDEX `idx_dispatch_enabled`(`enabled`) USING BTREE,
  INDEX `idx_dispatch_assignee`(`assignee_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '网格化自动派单规则表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for message_notice
-- ----------------------------
DROP TABLE IF EXISTS `message_notice`;
CREATE TABLE `message_notice`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '接收用户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息内容',
  `message_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息类型',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型',
  `business_id` bigint(20) NULL DEFAULT NULL COMMENT '业务ID',
  `is_read` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `read_time` datetime(0) NULL DEFAULT NULL COMMENT '已读时间',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '站内消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for neighbor_help
-- ----------------------------
DROP TABLE IF EXISTS `neighbor_help`;
CREATE TABLE `neighbor_help`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
  `help_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类型：SEEK-求助，OFFER-提供帮助',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类：DAILY-日常，SKILL-技能，ITEM-物品，OTHER-其他',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '发布人ID',
  `contact_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系方式',
  `images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-已关闭，1-进行中，2-已完成',
  `view_count` int(11) NULL DEFAULT 0 COMMENT '浏览次数',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '邻里互助表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
  `notice_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类型：NOTICE-通知，ANNOUNCEMENT-公告，NEWS-新闻',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'https://tu.ltyuanfang.cn/api/fengjing.php' COMMENT '封面图片',
  `is_top` tinyint(4) NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
  `publisher_id` bigint(20) NULL DEFAULT NULL COMMENT '发布人ID',
  `view_count` int(11) NULL DEFAULT 0 COMMENT '浏览次数',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-草稿，1-已发布，2-已下架',
  `publish_time` datetime(0) NULL DEFAULT NULL COMMENT '发布时间',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for notice_read_record
-- ----------------------------
DROP TABLE IF EXISTS `notice_read_record`;
CREATE TABLE `notice_read_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `notice_id` bigint(20) NOT NULL COMMENT '通知ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `read_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '阅读时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知阅读记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作描述',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '方法名',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '参数',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `time` bigint(20) NULL DEFAULT NULL COMMENT '执行时长(毫秒)',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for points_account
-- ----------------------------
DROP TABLE IF EXISTS `points_account`;
CREATE TABLE `points_account`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `total_points` int(11) NOT NULL DEFAULT 0 COMMENT '总积分',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '积分账户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for points_record
-- ----------------------------
DROP TABLE IF EXISTS `points_record`;
CREATE TABLE `points_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `change_type` tinyint(4) NOT NULL COMMENT '变动类型：1-增加，-1-扣减',
  `points` int(11) NOT NULL COMMENT '变动积分值(正数)',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务类型',
  `business_id` bigint(20) NOT NULL COMMENT '业务ID',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `operator_id` bigint(20) NULL DEFAULT NULL COMMENT '操作人ID',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_unique_event`(`user_id`, `business_type`, `business_id`, `change_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '积分流水表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resident
-- ----------------------------
DROP TABLE IF EXISTS `resident`;
CREATE TABLE `resident`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `gender` tinyint(4) NULL DEFAULT NULL COMMENT '性别：0-女，1-男',
  `birth_date` date NULL DEFAULT NULL COMMENT '出生日期',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '居住地址',
  `building_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '楼栋号',
  `unit_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单元号',
  `room_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '房间号',
  `residence_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '居住类型：OWN-自有，RENT-租住',
  `move_in_date` date NULL DEFAULT NULL COMMENT '入住日期',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-已迁出，1-在住',
  `photo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'https://tu.ltyuanfang.cn/api/fengjing.php' COMMENT '照片',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '居民信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for security_hazard
-- ----------------------------
DROP TABLE IF EXISTS `security_hazard`;
CREATE TABLE `security_hazard`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '隐患标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '隐患描述',
  `hazard_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '隐患类型：FIRE-消防，THEFT-盗窃，TRAFFIC-交通，OTHER-其他',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '隐患位置',
  `images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片',
  `reporter_id` bigint(20) NULL DEFAULT NULL COMMENT '上报人ID',
  `handler_id` bigint(20) NULL DEFAULT NULL COMMENT '处理人ID',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '状态：0-待处理，1-处理中，2-已解决，3-已关闭',
  `handle_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '处理结果',
  `handle_time` datetime(0) NULL DEFAULT NULL COMMENT '处理时间',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '治安隐患表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for service_appointment
-- ----------------------------
DROP TABLE IF EXISTS `service_appointment`;
CREATE TABLE `service_appointment`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `service_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务类型：REPAIR-维修，CLEAN-保洁，MEDICAL-医疗，OTHER-其他',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '预约标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '预约内容',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '预约人ID',
  `appointment_time` datetime(0) NULL DEFAULT NULL COMMENT '预约时间',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务地址',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '状态：0-待确认，1-已确认，2-已完成，3-已取消',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '服务预约表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for service_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `service_evaluation`;
CREATE TABLE `service_evaluation`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `work_order_id` bigint(20) NOT NULL COMMENT '工单ID',
  `user_id` bigint(20) NOT NULL COMMENT '评价用户ID（工单提交人）',
  `score` tinyint(4) NOT NULL COMMENT '评分(1-5)',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '评价内容',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_work_order_user`(`work_order_id`, `user_id`) USING BTREE,
  INDEX `idx_eval_user`(`user_id`) USING BTREE,
  INDEX `idx_eval_score`(`score`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '工单服务评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for service_guide
-- ----------------------------
DROP TABLE IF EXISTS `service_guide`;
CREATE TABLE `service_guide`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类：CERTIFICATE-证件办理，SOCIAL-社保，HOUSING-住房，OTHER-其他',
  `required_materials` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '所需材料',
  `process_steps` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '办理流程',
  `contact_info` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系方式',
  `view_count` int(11) NULL DEFAULT 0 COMMENT '浏览次数',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '办事指南表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码',
  `permission_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `permission_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限类型：MENU / BUTTON / DATA_SCOPE',
  `module_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模块标识',
  `parent_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '父级权限编码',
  `route_path` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '前端路由路径，仅菜单权限使用',
  `sort_no` int(11) NOT NULL DEFAULT 0 COMMENT '排序值',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_permission_code`(`permission_code`) USING BTREE,
  INDEX `idx_permission_type_status`(`permission_type`, `status`) USING BTREE,
  INDEX `idx_permission_module`(`module_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 92 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码：ADMIN / USER',
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_permission`(`role_code`, `permission_code`) USING BTREE,
  INDEX `idx_role_code`(`role_code`) USING BTREE,
  INDEX `idx_role_permission_code`(`permission_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 167 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（MD5示例值）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'https://tu.ltyuanfang.cn/api/fengjing.php' COMMENT '头像',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for volunteer
-- ----------------------------
DROP TABLE IF EXISTS `volunteer`;
CREATE TABLE `volunteer`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `skills` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '技能特长',
  `service_hours` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '服务时长',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '状态：0-待审核，1-已通过，2-已拒绝',
  `join_date` date NULL DEFAULT NULL COMMENT '加入日期',
  `photo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'https://tu.ltyuanfang.cn/api/fengjing.php' COMMENT '照片',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '志愿者表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for work_order
-- ----------------------------
DROP TABLE IF EXISTS `work_order`;
CREATE TABLE `work_order`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工单标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '工单内容',
  `order_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工单类型：REPAIR-报修，COMPLAINT-投诉，SUGGESTION-建议，OTHER-其他',
  `images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片，多个用逗号分隔',
  `submitter_id` bigint(20) NULL DEFAULT NULL COMMENT '提交人ID',
  `handler_id` bigint(20) NULL DEFAULT NULL COMMENT '处理人ID',
  `assignee_id` bigint(20) NULL DEFAULT NULL COMMENT '责任人ID',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '状态：0-待处理，1-处理中，2-已完成，3-已关闭',
  `priority` tinyint(4) NULL DEFAULT 1 COMMENT '优先级：0-低，1-中，2-高',
  `deadline` datetime(0) NULL DEFAULT NULL COMMENT '要求完成时间',
  `is_overtime` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否超时：0-否，1-是',
  `handle_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '处理结果',
  `handle_time` datetime(0) NULL DEFAULT NULL COMMENT '处理时间',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_work_order_deadline`(`deadline`) USING BTREE,
  INDEX `idx_work_order_assignee`(`assignee_id`) USING BTREE,
  INDEX `idx_work_order_overtime_status`(`is_overtime`, `status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '工单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for notice_subscription
-- ----------------------------
DROP TABLE IF EXISTS `notice_subscription`;
CREATE TABLE `notice_subscription`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `notice_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订阅通知类型',
  `receive_notification` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否接收通知',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_notice_subscription_user_type`(`user_id`, `notice_type`) USING BTREE,
  INDEX `idx_notice_subscription_user`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知订阅表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Demo data for sys_user
-- ----------------------------
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `email`, `role`, `status`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13800000001', 'admin@community.com', 'ADMIN', 1),
('zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13800000002', 'zhangsan@qq.com', 'USER', 1),
('lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', '13800000003', 'lisi@qq.com', 'USER', 1),
('wangwu', 'e10adc3949ba59abbe56e057f20f883e', '王五', '13800000004', 'wangwu@163.com', 'USER', 1),
('zhaoliu', 'e10adc3949ba59abbe56e057f20f883e', '赵六', '13800000005', 'zhaoliu@163.com', 'USER', 1),
('sunqi', 'e10adc3949ba59abbe56e057f20f883e', '孙七', '13800000006', 'sunqi@gmail.com', 'USER', 1),
('zhouba', 'e10adc3949ba59abbe56e057f20f883e', '周八', '13800000007', 'zhouba@gmail.com', 'ADMIN', 1),
('wujiu', 'e10adc3949ba59abbe56e057f20f883e', '吴九', '13800000008', 'wujiu@qq.com', 'USER', 1),
('zhengshi', 'e10adc3949ba59abbe56e057f20f883e', '郑十', '13800000009', 'zhengshi@qq.com', 'USER', 1),
('qianyi', 'e10adc3949ba59abbe56e057f20f883e', '钱一', '13800000010', 'qianyi@163.com', 'USER', 1);

-- ----------------------------
-- Demo data for resident
-- ----------------------------
INSERT INTO `resident` (`name`, `id_card`, `gender`, `birth_date`, `phone`, `address`, `building_no`, `unit_no`, `room_no`, `residence_type`, `move_in_date`, `status`, `remark`) VALUES
('陈晓明', '110101199001011234', 1, '1990-01-01', '13900000001', '幸福小区1号楼1单元101室', '1', '1', '101', 'OWN', '2018-05-15', 1, '业主委员会成员'),
('王丽华', '110101198512152345', 0, '1985-12-15', '13900000002', '幸福小区1号楼1单元102室', '1', '1', '102', 'OWN', '2019-03-20', 1, '业主'),
('李建国', '110101197803203456', 1, '1978-03-20', '13900000003', '幸福小区2号楼2单元201室', '2', '2', '201', 'OWN', '2017-08-10', 1, '退休教师'),
('张美玲', '110101200005054567', 0, '2000-05-05', '13900000004', '幸福小区2号楼2单元202室', '2', '2', '202', 'RENT', '2023-01-01', 1, '租户'),
('刘德华', '110101196808085678', 1, '1968-08-08', '13900000005', '幸福小区3号楼1单元301室', '3', '1', '301', 'OWN', '2015-06-18', 1, '小区志愿者'),
('赵小燕', '110101199206126789', 0, '1992-06-12', '13900000006', '幸福小区3号楼1单元302室', '3', '1', '302', 'RENT', '2024-02-28', 1, '租户'),
('孙伟强', '110101198811117890', 1, '1988-11-11', '13900000007', '幸福小区4号楼3单元401室', '4', '3', '401', 'OWN', '2020-09-01', 1, '党员'),
('周雪梅', '110101197502028901', 0, '1975-02-02', '13900000008', '幸福小区4号楼3单元402室', '4', '3', '402', 'OWN', '2016-12-25', 1, '楼栋长'),
('吴志远', '110101200108089012', 1, '2001-08-08', '13900000009', '幸福小区5号楼2单元501室', '5', '2', '501', 'RENT', '2025-01-10', 1, '大学生租户'),
('郑秀英', '110101195509090123', 0, '1955-09-09', '13900000010', '幸福小区5号楼2单元502室', '5', '2', '502', 'OWN', '2010-04-01', 1, '退休干部');

-- ----------------------------
-- Demo data for family_relation
-- ----------------------------
INSERT INTO `family_relation` (`resident_id`, `related_resident_id`, `relation`) VALUES
(1, 2, 'SPOUSE'), (2, 1, 'SPOUSE'),
(3, 4, 'PARENT'), (4, 3, 'CHILD'),
(5, 6, 'SIBLING'), (6, 5, 'SIBLING'),
(7, 8, 'SPOUSE'), (8, 7, 'SPOUSE');

-- ----------------------------
-- Demo data for community_activity
-- ----------------------------
INSERT INTO `community_activity` (`title`, `content`, `cover_image`, `activity_type`, `location`, `start_time`, `end_time`, `is_cancelled`, `organizer_id`) VALUES
('春节联欢晚会', '社区居民春节联欢活动，包含文艺表演、抽奖等环节', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'CULTURE', '社区活动中心大厅', '2025-01-25 19:00:00', '2025-01-25 22:00:00', 0, 1),
('社区篮球赛', '社区居民篮球友谊赛，欢迎篮球爱好者报名参加', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'SPORT', '社区篮球场', '2025-01-20 09:00:00', '2025-01-20 17:00:00', 0, 1),
('志愿者清洁活动', '社区环境清洁志愿活动，共建美好家园', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'VOLUNTEER', '社区各公共区域', '2025-01-15 08:00:00', '2025-01-15 12:00:00', 0, 7),
('老年人健康讲座', '邀请三甲医院专家为老年居民讲解健康养生知识', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'OTHER', '社区会议室', '2025-01-10 14:00:00', '2025-01-10 16:00:00', 0, 1),
('亲子手工活动', '家长与孩子一起制作手工艺品，增进亲子关系', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'CULTURE', '社区儿童活动室', '2025-01-18 10:00:00', '2025-01-18 12:00:00', 0, 7),
('广场舞比赛', '社区广场舞队伍展示比赛，设有最佳表演奖', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'SPORT', '社区广场', '2025-01-22 18:00:00', '2025-01-22 20:00:00', 0, 1),
('垃圾分类宣传活动', '普及垃圾分类知识，参与者可获得环保袋', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'VOLUNTEER', '社区广场', '2025-01-08 09:00:00', '2025-01-08 11:00:00', 0, 7),
('元宵节猜灯谜', '元宵佳节猜灯谜活动，猜中有奖', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'CULTURE', '社区广场', '2025-02-12 18:00:00', '2025-02-12 20:00:00', 0, 1),
('已取消的音乐会', '因天气原因取消的户外音乐会', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'CULTURE', '社区广场', '2025-01-05 19:00:00', '2025-01-05 21:00:00', 1, 1),
('社区义诊活动', '邀请医院专家为居民免费体检', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'OTHER', '社区服务中心', '2025-01-30 08:00:00', '2025-01-30 17:00:00', 0, 7);

-- ----------------------------
-- Demo data for activity_registration
-- ----------------------------
INSERT INTO `activity_registration` (`activity_id`, `user_id`, `status`, `register_time`, `check_in_time`) VALUES
(1, 2, 0, '2025-01-05 10:00:00', NULL), (1, 3, 0, '2025-01-05 11:00:00', NULL),
(1, 4, 0, '2025-01-06 09:00:00', NULL), (1, 5, 0, '2025-01-06 14:00:00', NULL),
(2, 2, 0, '2025-01-08 14:00:00', NULL), (2, 5, 0, '2025-01-08 15:00:00', NULL),
(3, 3, 1, '2025-01-10 08:00:00', '2025-01-15 08:05:00'), (3, 4, 1, '2025-01-10 09:00:00', '2025-01-15 08:10:00'),
(4, 6, 1, '2025-01-08 10:00:00', '2025-01-10 13:55:00'), (4, 8, 1, '2025-01-09 11:00:00', '2025-01-10 14:00:00'),
(5, 2, 0, '2025-01-12 09:00:00', NULL), (5, 3, 2, '2025-01-12 10:00:00', NULL);

-- ----------------------------
-- Demo data for work_order
-- ----------------------------
INSERT INTO `work_order` (`title`, `content`, `order_type`, `images`, `submitter_id`, `handler_id`, `status`, `priority`, `handle_result`, `handle_time`, `create_time`) VALUES
('楼道灯不亮', '1号楼2单元3楼楼道灯损坏，晚上很黑不安全', 'REPAIR', 'https://tu.ltyuanfang.cn/api/fengjing.php', 2, 1, 2, 1, '已更换灯泡，恢复正常', '2026-01-08 15:00:00', '2026-01-05 10:00:00'),
('邻居噪音扰民', '楼上住户经常深夜制造噪音，严重影响休息', 'COMPLAINT', NULL, 3, 1, 1, 2, NULL, NULL, '2026-01-06 14:00:00'),
('建议增设健身器材', '建议在小区广场增设一些健身器材', 'SUGGESTION', NULL, 4, NULL, 0, 0, NULL, NULL, '2026-01-07 09:00:00'),
('电梯故障', '2号楼电梯经常出现异响，存在安全隐患', 'REPAIR', 'https://tu.ltyuanfang.cn/api/fengjing.php', 5, 7, 1, 2, NULL, NULL, '2026-01-08 11:00:00'),
('垃圾桶满溢', '3号楼门口垃圾桶已满，无人清理', 'COMPLAINT', 'https://tu.ltyuanfang.cn/api/fengjing.php', 6, 1, 2, 1, '已通知保洁清理完毕', '2026-01-09 10:00:00', '2026-01-09 08:00:00'),
('路灯损坏', '小区东门附近路灯损坏，夜间出行不便', 'REPAIR', NULL, 2, NULL, 0, 1, NULL, NULL, '2026-01-10 16:00:00'),
('下水道堵塞', '4号楼1单元下水道堵塞，污水外溢', 'REPAIR', 'https://tu.ltyuanfang.cn/api/fengjing.php', 3, 7, 2, 2, '已疏通下水道', '2026-01-10 11:00:00', '2026-01-10 09:00:00'),
('门禁卡消磁', '门禁卡无法正常使用，需要重新办理', 'OTHER', NULL, 5, 1, 2, 0, '已补办新卡', '2026-01-06 14:00:00', '2026-01-06 10:00:00'),
('暖气不热', '家里暖气温度不够，室温只有15度', 'REPAIR', NULL, 2, 1, 2, 2, '已调整供暖阀门', '2025-12-20 14:00:00', '2025-12-18 09:00:00'),
('停车位被占', '我的固定车位经常被外来车辆占用', 'COMPLAINT', NULL, 3, 1, 2, 1, '已加强巡逻管理', '2025-12-22 10:00:00', '2025-12-20 15:00:00'),
('建议增加快递柜', '小区快递柜数量不足，建议增加', 'SUGGESTION', NULL, 4, NULL, 2, 0, '已采纳，计划下月安装', '2025-12-25 11:00:00', '2025-12-15 14:00:00'),
('窗户漏风', '卧室窗户密封不好，冬天漏风', 'REPAIR', NULL, 5, 7, 2, 1, '已更换密封条', '2025-12-28 16:00:00', '2025-12-25 10:00:00'),
('物业费疑问', '对本月物业费账单有疑问', 'OTHER', NULL, 6, 1, 2, 0, '已解释清楚', '2025-12-30 09:00:00', '2025-12-28 11:00:00'),
('年底安全检查', '年底消防安全检查发现问题', 'REPAIR', NULL, 2, 7, 2, 2, '已整改', '2025-12-30 16:00:00', '2025-12-29 10:00:00'),
('楼顶漏水', '顶楼住户反映下雨天楼顶漏水', 'REPAIR', 'https://tu.ltyuanfang.cn/api/fengjing.php', 2, 7, 2, 2, '已修补防水层', '2025-11-20 15:00:00', '2025-11-15 08:00:00'),
('广场舞噪音', '晚上广场舞音乐声音太大', 'COMPLAINT', NULL, 3, 1, 2, 1, '已协调降低音量', '2025-11-18 10:00:00', '2025-11-16 19:00:00'),
('建议延长物业服务时间', '希望物业服务时间能延长到晚上8点', 'SUGGESTION', NULL, 4, NULL, 2, 0, '已采纳调整', '2025-11-25 14:00:00', '2025-11-20 09:00:00'),
('门锁故障', '单元门电子锁经常打不开', 'REPAIR', NULL, 5, 1, 2, 2, '已更换门锁', '2025-11-28 11:00:00', '2025-11-25 16:00:00'),
('供暖问题', '暖气片不热需要检修', 'REPAIR', NULL, 6, 7, 2, 1, '已维修', '2025-11-30 10:00:00', '2025-11-28 09:00:00'),
('绿化带杂草', '小区绿化带杂草丛生需要清理', 'COMPLAINT', NULL, 2, 7, 2, 0, '已安排清理', '2025-10-20 10:00:00', '2025-10-18 14:00:00'),
('电表故障', '电表显示异常，用电量不准确', 'REPAIR', NULL, 3, 1, 2, 1, '已更换电表', '2025-10-25 15:00:00', '2025-10-22 09:00:00'),
('建议增设监控', '建议在停车场增设监控摄像头', 'SUGGESTION', NULL, 4, NULL, 2, 1, '已列入计划', '2025-10-28 11:00:00', '2025-10-25 10:00:00'),
('国庆期间噪音', '国庆期间装修噪音扰民', 'COMPLAINT', NULL, 5, 1, 2, 1, '已协调', '2025-10-08 14:00:00', '2025-10-05 10:00:00'),
('空调外机噪音', '邻居空调外机噪音太大', 'COMPLAINT', NULL, 5, 1, 2, 1, '已协调处理', '2025-09-20 14:00:00', '2025-09-18 20:00:00'),
('墙面脱落', '楼道墙面涂料脱落', 'REPAIR', 'https://tu.ltyuanfang.cn/api/fengjing.php', 6, 7, 2, 0, '已重新粉刷', '2025-09-25 16:00:00', '2025-09-22 11:00:00'),
('建议增加休息座椅', '小区步道建议增加休息座椅', 'SUGGESTION', NULL, 2, NULL, 2, 0, '已采纳', '2025-09-28 10:00:00', '2025-09-25 15:00:00'),
('水管爆裂', '地下室水管爆裂漏水', 'REPAIR', 'https://tu.ltyuanfang.cn/api/fengjing.php', 3, 1, 2, 2, '已紧急抢修', '2025-08-15 18:00:00', '2025-08-15 10:00:00'),
('乱扔垃圾', '有人从楼上乱扔垃圾', 'COMPLAINT', NULL, 4, 7, 2, 2, '已加强监管', '2025-08-20 11:00:00', '2025-08-18 09:00:00'),
('高温天气用电问题', '夏季用电高峰期电压不稳', 'REPAIR', NULL, 5, 1, 2, 2, '已联系供电局', '2025-08-25 15:00:00', '2025-08-22 10:00:00'),
('电梯按钮失灵', '3号楼电梯部分按钮失灵', 'REPAIR', NULL, 5, 1, 2, 1, '已维修', '2025-07-20 14:00:00', '2025-07-18 08:00:00'),
('遛狗不牵绳', '有居民遛狗不牵绳，存在安全隐患', 'COMPLAINT', NULL, 6, 1, 2, 1, '已加强宣传', '2025-07-25 10:00:00', '2025-07-22 17:00:00'),
('空调漏水', '空调外机漏水影响楼下', 'COMPLAINT', NULL, 2, 7, 2, 1, '已处理', '2025-07-28 11:00:00', '2025-07-26 09:00:00'),
('建议增加遮阳棚', '停车场建议增加遮阳棚', 'SUGGESTION', NULL, 3, NULL, 2, 0, '已采纳', '2025-07-30 10:00:00', '2025-07-28 14:00:00'),
('路面破损', '小区内部道路有坑洼', 'REPAIR', 'https://tu.ltyuanfang.cn/api/fengjing.php', 2, 7, 2, 1, '已修补', '2025-06-20 15:00:00', '2025-06-18 11:00:00'),
('建议增加儿童游乐设施', '希望增加更多儿童游乐设施', 'SUGGESTION', NULL, 3, NULL, 2, 0, '已列入计划', '2025-06-25 09:00:00', '2025-06-22 14:00:00'),
('蚊虫问题', '小区绿化带蚊虫较多', 'COMPLAINT', NULL, 4, 1, 2, 0, '已安排消杀', '2025-06-28 10:00:00', '2025-06-25 16:00:00'),
('消防栓漏水', '1号楼消防栓漏水', 'REPAIR', NULL, 4, 1, 2, 2, '已维修', '2025-05-18 11:00:00', '2025-05-15 09:00:00'),
('违规装修噪音', '邻居装修噪音超时', 'COMPLAINT', NULL, 5, 7, 2, 1, '已协调', '2025-05-22 14:00:00', '2025-05-20 08:00:00'),
('五一期间安保', '五一期间安保问题反馈', 'OTHER', NULL, 6, 1, 2, 0, '已加强', '2025-05-05 10:00:00', '2025-05-02 09:00:00'),
('草坪需要修剪', '小区草坪长得太高需要修剪', 'COMPLAINT', NULL, 6, 1, 2, 0, '已安排修剪', '2025-04-20 10:00:00', '2025-04-18 15:00:00'),
('建议增加垃圾分类桶', '建议增加垃圾分类投放点', 'SUGGESTION', NULL, 2, NULL, 2, 0, '已采纳', '2025-04-25 11:00:00', '2025-04-22 09:00:00'),
('春季绿化问题', '部分绿化带植物枯死', 'REPAIR', NULL, 3, 7, 2, 0, '已补种', '2025-04-28 14:00:00', '2025-04-25 10:00:00'),
('楼道堆放杂物', '有居民在楼道堆放杂物', 'COMPLAINT', NULL, 3, 7, 2, 1, '已清理', '2025-03-20 14:00:00', '2025-03-18 10:00:00'),
('门禁系统故障', '单元门禁系统故障', 'REPAIR', NULL, 4, 1, 2, 2, '已修复', '2025-03-25 16:00:00', '2025-03-22 11:00:00'),
('建议增加充电桩', '电动车充电桩不够用', 'SUGGESTION', NULL, 5, NULL, 2, 1, '已规划', '2025-03-28 10:00:00', '2025-03-25 09:00:00'),
('暖气管道漏水', '暖气管道接口处漏水', 'REPAIR', NULL, 5, 1, 2, 2, '已维修', '2025-02-18 15:00:00', '2025-02-15 09:00:00'),
('停车乱象', '外来车辆乱停乱放', 'COMPLAINT', NULL, 6, 7, 2, 1, '已加强管理', '2025-02-22 10:00:00', '2025-02-20 14:00:00'),
('春节期间值班问题', '春节期间物业值班人员不足', 'OTHER', NULL, 2, 1, 2, 1, '已调整', '2025-02-10 11:00:00', '2025-02-08 10:00:00');

-- ----------------------------
-- Demo data for volunteer
-- ----------------------------
INSERT INTO `volunteer` (`user_id`, `name`, `phone`, `skills`, `service_hours`, `status`, `join_date`) VALUES
(2, '张三', '13800000002', '电脑维修、文案写作', 45.50, 1, '2023-05-01'),
(3, '李四', '13800000003', '医疗护理、心理咨询', 120.00, 1, '2022-03-15'),
(4, '王五', '13800000004', '法律咨询、纠纷调解', 30.00, 1, '2024-01-10'),
(5, '赵六', '13800000005', '摄影摄像、视频剪辑', 15.50, 0, NULL),
(6, '孙七', '13800000006', '手工制作、绘画教学', 60.00, 1, '2023-08-20'),
(8, '吴九', '13800000008', '水电维修、家电维修', 80.00, 1, '2022-11-01');

-- ----------------------------
-- Demo data for floating_population
-- ----------------------------
INSERT INTO `floating_population` (`name`, `id_card`, `gender`, `phone`, `origin_place`, `current_address`, `work_unit`, `register_date`, `expected_leave_date`, `status`, `remark`) VALUES
('李明', '320101199505051234', 1, '13700000001', '江苏省南京市', '幸福小区1号楼1单元103室', '某科技公司', '2024-06-01', '2025-06-01', 1, '程序员'),
('王芳', '330101199208082345', 0, '13700000002', '浙江省杭州市', '幸福小区2号楼1单元201室', '某电商公司', '2024-03-15', '2025-03-15', 1, '运营专员'),
('张伟', '410101198812123456', 1, '13700000003', '河南省郑州市', '幸福小区3号楼2单元301室', '某建筑公司', '2024-09-01', '2025-03-01', 1, '工程师'),
('刘洋', '510101199703034567', 1, '13700000004', '四川省成都市', '幸福小区4号楼1单元401室', '某餐饮公司', '2024-01-10', '2024-12-31', 0, '已离开'),
('陈静', '420101199606065678', 0, '13700000005', '湖北省武汉市', '幸福小区5号楼3单元501室', '某医院', '2024-07-20', '2025-07-20', 1, '护士'),
('赵强', '370101199404046789', 1, '13700000006', '山东省济南市', '幸福小区1号楼2单元102室', '某物流公司', '2024-11-01', '2025-11-01', 1, '快递员'),
('孙丽', '340101199809087890', 0, '13700000007', '安徽省合肥市', '幸福小区2号楼3单元202室', '某教育机构', '2024-08-15', '2025-08-15', 1, '教师'),
('周杰', '610101199201018901', 1, '13700000008', '陕西省西安市', '幸福小区3号楼1单元302室', '自由职业', '2024-05-01', '2025-05-01', 1, '设计师');

-- ----------------------------
-- Demo data for security_hazard
-- ----------------------------
INSERT INTO `security_hazard` (`title`, `content`, `hazard_type`, `location`, `images`, `reporter_id`, `handler_id`, `status`, `handle_result`, `handle_time`) VALUES
('消防通道堆放杂物', '1号楼1单元消防通道被大量杂物堵塞', 'FIRE', '幸福小区1号楼1单元', 'https://tu.ltyuanfang.cn/api/fengjing.php', 2, 1, 2, '已清理完毕', '2025-01-05 14:00:00'),
('地下车库照明故障', '地下车库B区多处照明灯损坏', 'OTHER', '幸福小区地下车库B区', 'https://tu.ltyuanfang.cn/api/fengjing.php', 3, 1, 1, NULL, NULL),
('东门门禁损坏', '东门门禁系统故障，无法正常刷卡', 'THEFT', '幸福小区东门', 'https://tu.ltyuanfang.cn/api/fengjing.php', 4, NULL, 0, NULL, NULL),
('电动车违规充电', '3号楼有居民在楼道内给电动车充电', 'FIRE', '幸福小区3号楼2单元', 'https://tu.ltyuanfang.cn/api/fengjing.php', 5, 7, 2, '已劝导居民移至充电桩', '2025-01-08 10:00:00'),
('小区内车辆超速', '有车辆在小区内超速行驶', 'TRAFFIC', '幸福小区内部道路', 'https://tu.ltyuanfang.cn/api/fengjing.php', 6, 1, 1, NULL, NULL),
('围墙破损', '小区北侧围墙有一处破损', 'THEFT', '幸福小区北侧围墙', 'https://tu.ltyuanfang.cn/api/fengjing.php', 2, 7, 2, '已修复围墙', '2025-01-02 16:00:00'),
('灭火器过期', '5号楼各楼层灭火器已超过有效期', 'FIRE', '幸福小区5号楼', 'https://tu.ltyuanfang.cn/api/fengjing.php', 8, NULL, 0, NULL, NULL),
('儿童游乐设施松动', '儿童游乐区秋千座椅螺丝松动', 'OTHER', '幸福小区儿童游乐区', 'https://tu.ltyuanfang.cn/api/fengjing.php', 3, 1, 2, '已更换螺丝并加固', '2025-01-10 09:30:00');

-- ----------------------------
-- Demo data for service_guide
-- ----------------------------
INSERT INTO `service_guide` (`title`, `content`, `category`, `required_materials`, `process_steps`, `contact_info`, `view_count`, `status`) VALUES
('居住证办理指南', '外来人员在本社区居住满半年可申请办理居住证', 'CERTIFICATE', '1.身份证原件及复印件\n2.房屋租赁合同\n3.近期1寸白底照片2张', '1.准备所需材料\n2.到社区服务中心领取申请表\n3.填写申请表并提交材料\n4.等待审核', '社区服务中心：010-12345678', 256, 1),
('医保报销流程', '城镇居民医疗保险报销办理流程', 'SOCIAL', '1.医保卡\n2.身份证\n3.医院收费票据原件\n4.费用明细清单', '1.出院时在医院医保窗口办理\n2.或携带材料到社保中心办理', '社保服务热线：12333', 189, 1),
('老年优待证办理', '60周岁以上老年人可申请办理老年优待证', 'CERTIFICATE', '1.身份证原件及复印件\n2.近期1寸彩色照片1张\n3.户口本', '1.携带材料到社区服务中心\n2.填写申请表\n3.现场审核通过即可领取', '社区服务中心：010-12345678', 145, 1),
('公租房申请指南', '符合条件的居民可申请公共租赁住房', 'HOUSING', '1.身份证、户口本\n2.收入证明\n3.住房情况证明', '1.到社区领取并填写申请表\n2.提交材料进行初审\n3.街道办复审\n4.区住建局终审', '住房保障热线：010-87654321', 312, 1),
('生育登记服务', '计划生育登记及相关证明办理', 'CERTIFICATE', '1.夫妻双方身份证\n2.结婚证\n3.户口本', '1.网上预约或现场取号\n2.提交材料\n3.当场办结', '计生服务电话：010-11112222', 98, 1),
('养老保险查询', '城镇职工养老保险缴费记录及待遇查询', 'SOCIAL', '1.身份证\n2.社保卡', '1.携带证件到社保中心查询\n2.或登录社保网站在线查询', '社保服务热线：12333', 276, 1);

-- ----------------------------
-- Demo data for service_appointment
-- ----------------------------
INSERT INTO `service_appointment` (`service_type`, `title`, `content`, `user_id`, `appointment_time`, `contact_name`, `contact_phone`, `address`, `status`, `remark`) VALUES
('REPAIR', '水管漏水维修', '厨房水管接口处漏水，需要更换密封圈', 2, '2025-01-12 09:00:00', '张三', '13800000002', '幸福小区1号楼1单元101室', 1, '已安排维修师傅上门'),
('CLEAN', '家庭深度保洁', '春节前家庭大扫除，约120平米', 3, '2025-01-15 14:00:00', '李四', '13800000003', '幸福小区2号楼2单元201室', 0, '需要擦玻璃'),
('MEDICAL', '上门量血压', '老人行动不便，需要医护人员上门', 4, '2025-01-11 10:00:00', '王五', '13800000004', '幸福小区3号楼1单元301室', 2, '已完成，血压正常'),
('REPAIR', '空调不制热维修', '客厅空调开机后不制热', 5, '2025-01-13 15:00:00', '赵六', '13800000005', '幸福小区4号楼3单元401室', 1, '需要检查制冷剂'),
('OTHER', '开锁服务', '钥匙丢失，需要开锁服务', 6, '2025-01-10 18:30:00', '孙七', '13800000006', '幸福小区5号楼2单元501室', 2, '已开锁并更换锁芯'),
('CLEAN', '油烟机清洗', '油烟机使用多年，油污严重', 2, '2025-01-18 10:00:00', '张三', '13800000002', '幸福小区1号楼1单元101室', 0, NULL),
('MEDICAL', '慢性病随访', '糖尿病患者定期随访', 8, '2025-01-14 09:30:00', '吴九', '13800000008', '幸福小区4号楼3单元402室', 1, '社区医生已确认'),
('REPAIR', '马桶堵塞疏通', '卫生间马桶堵塞，冲水不畅', 3, '2025-01-11 11:00:00', '李四', '13800000003', '幸福小区2号楼2单元201室', 3, '用户自行解决，取消预约');

-- ----------------------------
-- Demo data for neighbor_help
-- ----------------------------
INSERT INTO `neighbor_help` (`title`, `content`, `help_type`, `category`, `user_id`, `contact_info`, `images`, `status`, `view_count`) VALUES
('求助：需要人帮忙照看小孩', '周末临时有事外出，需要邻居帮忙照看小孩2小时', 'SEEK', 'DAILY', 2, '13800000002', NULL, 1, 45),
('提供：免费辅导小学数学', '退休教师，可免费辅导小学生数学', 'OFFER', 'SKILL', 3, '13800000003', NULL, 1, 89),
('求助：借用电钻', '装修需要借用电钻一天，用完立即归还', 'SEEK', 'ITEM', 4, '13800000004', NULL, 2, 23),
('提供：闲置婴儿车转让', '九成新婴儿车，低价转让200元', 'OFFER', 'ITEM', 5, '13800000005', 'https://tu.ltyuanfang.cn/api/fengjing.php', 1, 67),
('求助：寻找走失的猫咪', '橘色猫咪走失，名叫橘子，如有发现请联系', 'SEEK', 'OTHER', 6, '13800000006', 'https://tu.ltyuanfang.cn/api/fengjing.php', 1, 156),
('提供：代收快递服务', '白天在家，可帮邻居代收快递', 'OFFER', 'DAILY', 8, '13800000008', NULL, 1, 34),
('求助：需要搬家帮手', '下周六搬家需要2-3人帮忙搬重物', 'SEEK', 'DAILY', 2, '13800000002', NULL, 0, 28),
('提供：旧书籍免费赠送', '整理出一批旧书，免费赠送给需要的邻居', 'OFFER', 'ITEM', 3, '13800000003', 'https://tu.ltyuanfang.cn/api/fengjing.php', 1, 78);

-- ----------------------------
-- Demo data for notice
-- ----------------------------
INSERT INTO `notice` (`title`, `content`, `notice_type`, `cover_image`, `is_top`, `publisher_id`, `view_count`, `status`, `publish_time`) VALUES
('关于春节期间物业服务安排的通知', '春节期间（1月28日-2月4日）物业服务时间调整为9:00-17:00，紧急事务请拨打24小时值班电话。', 'NOTICE', 'https://tu.ltyuanfang.cn/api/fengjing.php', 1, 1, 523, 1, '2025-01-10 09:00:00'),
('社区2025年工作计划公告', '2025年社区将重点推进智慧社区建设、老旧设施改造等工作。', 'ANNOUNCEMENT', 'https://tu.ltyuanfang.cn/api/fengjing.php', 0, 1, 234, 1, '2025-01-05 10:00:00'),
('停水通知', '因管道维修，1月15日8:00-18:00，1-3号楼将停水，请提前做好储水准备。', 'NOTICE', 'https://tu.ltyuanfang.cn/api/fengjing.php', 1, 7, 456, 1, '2025-01-12 14:00:00'),
('社区志愿者招募公告', '社区现招募志愿者，有意者请到社区服务中心报名。', 'ANNOUNCEMENT', 'https://tu.ltyuanfang.cn/api/fengjing.php', 0, 1, 189, 1, '2025-01-08 11:00:00'),
('电梯维保通知', '1月20日将对各楼栋电梯进行例行维保，届时电梯将暂停使用2小时。', 'NOTICE', 'https://tu.ltyuanfang.cn/api/fengjing.php', 0, 7, 312, 1, '2025-01-11 15:00:00'),
('社区文艺汇演圆满结束', '2025年社区迎新文艺汇演于1月8日成功举办，感谢各位居民的参与。', 'NEWS', 'https://tu.ltyuanfang.cn/api/fengjing.php', 0, 1, 278, 1, '2025-01-09 16:00:00'),
('垃圾分类宣传', '请各位居民按照规定进行垃圾分类，共建美好家园。', 'NOTICE', 'https://tu.ltyuanfang.cn/api/fengjing.php', 0, 1, 145, 1, '2025-01-06 09:00:00'),
('消防安全提醒', '冬季干燥，请注意用火用电安全，不要在楼道堆放杂物。', 'NOTICE', 'https://tu.ltyuanfang.cn/api/fengjing.php', 0, 7, 198, 1, '2025-01-07 10:00:00');

-- ----------------------------
-- Demo data for notice_read_record
-- ----------------------------
INSERT INTO `notice_read_record` (`notice_id`, `user_id`, `read_time`) VALUES
(1, 2, '2025-01-10 10:00:00'), (1, 3, '2025-01-10 11:00:00'), (1, 4, '2025-01-10 12:00:00'),
(1, 5, '2025-01-10 13:00:00'), (1, 6, '2025-01-10 14:00:00'),
(2, 2, '2025-01-05 14:00:00'), (2, 3, '2025-01-06 09:00:00'),
(3, 2, '2025-01-12 15:00:00'), (3, 3, '2025-01-12 16:00:00'), (3, 4, '2025-01-12 17:00:00'),
(4, 5, '2025-01-08 14:00:00'), (4, 6, '2025-01-09 10:00:00'),
(5, 2, '2025-01-11 16:00:00'), (5, 3, '2025-01-11 17:00:00'),
(6, 2, '2025-01-09 17:00:00'), (6, 4, '2025-01-09 18:00:00');

-- ----------------------------
-- Demo data for notice_subscription
-- ----------------------------
INSERT INTO `notice_subscription` (`user_id`, `notice_type`, `receive_notification`) VALUES
(2, 'NOTICE', 1),
(2, 'ANNOUNCEMENT', 1),
(3, 'NOTICE', 1),
(3, 'NEWS', 0),
(4, 'NOTICE', 1),
(5, 'ANNOUNCEMENT', 1);

-- ----------------------------
-- Demo data for operation_log
-- ----------------------------
INSERT INTO `operation_log` (`user_id`, `username`, `operation`, `method`, `ip`, `time`, `create_time`) VALUES
(1, 'admin', '用户登录', 'com.community.controller.AuthController.login', '192.168.1.100', 45, '2025-01-10 08:30:00'),
(1, 'admin', '查询居民列表', 'com.community.controller.ResidentController.list', '192.168.1.100', 32, '2025-01-10 08:35:00'),
(1, 'admin', '新增居民信息', 'com.community.controller.ResidentController.add', '192.168.1.100', 28, '2025-01-10 09:00:00'),
(2, 'zhangsan', '用户登录', 'com.community.controller.AuthController.login', '192.168.1.101', 38, '2025-01-10 09:15:00'),
(2, 'zhangsan', '查询活动列表', 'com.community.controller.ActivityController.list', '192.168.1.101', 25, '2025-01-10 09:20:00'),
(1, 'admin', '处理工单', 'com.community.controller.WorkOrderController.handle', '192.168.1.100', 56, '2025-01-10 10:00:00'),
(3, 'lisi', '用户登录', 'com.community.controller.AuthController.login', '192.168.1.102', 35, '2025-01-10 10:30:00'),
(3, 'lisi', '提交工单', 'com.community.controller.WorkOrderController.add', '192.168.1.102', 48, '2025-01-10 10:35:00'),
(7, 'zhouba', '用户登录', 'com.community.controller.AuthController.login', '192.168.1.103', 40, '2025-01-10 11:00:00'),
(7, 'zhouba', '发布通知公告', 'com.community.controller.NoticeController.add', '192.168.1.103', 52, '2025-01-10 11:15:00'),
(1, 'admin', '审核志愿者', 'com.community.controller.VolunteerController.audit', '192.168.1.100', 30, '2025-01-10 14:00:00');

-- ----------------------------
-- Demo data for points_account / points_record
-- ----------------------------
INSERT INTO `points_account` (`user_id`, `total_points`, `create_time`, `update_time`) VALUES
(3, 10, '2025-01-15 08:06:00', '2025-01-15 08:06:00'),
(4, 10, '2025-01-15 08:11:00', '2025-01-15 08:11:00'),
(6, 10, '2025-01-10 13:56:00', '2025-01-10 13:56:00'),
(8, 10, '2025-01-10 14:01:00', '2025-01-10 14:01:00');

INSERT INTO `points_record` (`user_id`, `change_type`, `points`, `business_type`, `business_id`, `remark`, `operator_id`, `create_time`) VALUES
(3, 1, 10, 'ACTIVITY_CHECKIN', 7, '活动签到奖励积分', 3, '2025-01-15 08:05:30'),
(4, 1, 10, 'ACTIVITY_CHECKIN', 8, '活动签到奖励积分', 4, '2025-01-15 08:10:30'),
(6, 1, 10, 'ACTIVITY_CHECKIN', 9, '活动签到奖励积分', 6, '2025-01-10 13:55:30'),
(8, 1, 10, 'ACTIVITY_CHECKIN', 10, '活动签到奖励积分', 8, '2025-01-10 14:00:30');

-- ----------------------------
-- Demo data for message_notice
-- ----------------------------
INSERT INTO `message_notice` (`user_id`, `title`, `content`, `message_type`, `business_type`, `business_id`, `is_read`, `read_time`, `create_time`) VALUES
(2, '活动报名成功', '您已成功报名活动，活动ID：1', 'ACTIVITY', 'ACTIVITY_REGISTRATION', 1, 1, '2025-01-05 10:10:00', '2025-01-05 10:00:00'),
(3, '活动报名成功', '您已成功报名活动，活动ID：1', 'ACTIVITY', 'ACTIVITY_REGISTRATION', 2, 0, NULL, '2025-01-05 11:00:00'),
(2, '工单已完成', '您提交的工单[楼道灯不亮]已处理完成。', 'WORK_ORDER', 'WORK_ORDER', 1, 0, NULL, '2026-01-08 15:01:00'),
(5, '志愿者申请审核结果', '您的志愿者申请当前状态：待审核', 'VOLUNTEER', 'VOLUNTEER', 4, 0, NULL, '2025-01-10 09:00:00'),
(1, '有新的工单待处理', '系统已为您自动派单：[楼道灯不亮]，请及时处理。', 'SYSTEM', 'WORK_ORDER', 1, 1, '2026-01-05 10:10:00', '2026-01-05 10:05:00'),
(7, '有新的工单待处理', '系统已为您自动派单：[电梯故障]，请及时处理。', 'SYSTEM', 'WORK_ORDER', 4, 0, NULL, '2026-01-08 11:05:00');

-- ----------------------------
-- Demo data for service_evaluation
-- ----------------------------
INSERT INTO `service_evaluation` (`work_order_id`, `user_id`, `score`, `content`, `create_time`, `update_time`) VALUES
(1, 2, 5, '处理非常及时，楼道照明恢复后晚上出行安全多了。', '2026-01-08 18:00:00', '2026-01-08 18:00:00'),
(5, 6, 4, '垃圾清理得比较快，希望后续能增加巡检频率。', '2026-01-09 12:00:00', '2026-01-09 12:00:00'),
(9, 2, 5, '供暖问题解决得很彻底，室内温度恢复正常。', '2025-12-20 18:00:00', '2025-12-20 18:00:00');

-- ----------------------------
-- Demo data for grid_dispatch_rule
-- ----------------------------
INSERT INTO `grid_dispatch_rule` (`grid_name`, `order_type`, `priority`, `assignee_id`, `enabled`, `remark`) VALUES
('默认报修派单规则', 'REPAIR', NULL, 1, 1, '报修类默认派给管理员'),
('投诉优先派单规则', 'COMPLAINT', NULL, 7, 1, '投诉类默认派给周八'),
('高优先级兜底规则', NULL, 2, 1, 1, '高优先级任务统一派给管理员');

-- ----------------------------
-- Demo updates for overtime / assignee
-- ----------------------------
UPDATE `work_order` SET `assignee_id` = 1, `deadline` = DATE_SUB(NOW(), INTERVAL 2 DAY), `is_overtime` = 1 WHERE `id` = 2;
UPDATE `work_order` SET `assignee_id` = 7, `deadline` = DATE_SUB(NOW(), INTERVAL 1 DAY), `is_overtime` = 1 WHERE `id` = 3;
UPDATE `work_order` SET `assignee_id` = 7, `deadline` = TIMESTAMP(CURDATE(), '18:00:00'), `is_overtime` = 0 WHERE `id` = 4;
UPDATE `work_order` SET `assignee_id` = 1, `deadline` = DATE_ADD(NOW(), INTERVAL 2 DAY), `is_overtime` = 0 WHERE `id` = 6;

-- ----------------------------
-- Extra indexes
-- ----------------------------
ALTER TABLE `sys_user` ADD INDEX `idx_phone` (`phone`);
ALTER TABLE `sys_user` ADD INDEX `idx_create_time` (`create_time`);
ALTER TABLE `resident` ADD INDEX `idx_id_card` (`id_card`);
ALTER TABLE `resident` ADD INDEX `idx_phone` (`phone`);
ALTER TABLE `resident` ADD INDEX `idx_address` (`address`);
ALTER TABLE `resident` ADD INDEX `idx_building_unit_room` (`building_no`, `unit_no`, `room_no`);
ALTER TABLE `resident` ADD INDEX `idx_create_time` (`create_time`);
ALTER TABLE `community_activity` ADD INDEX `idx_title` (`title`);
ALTER TABLE `community_activity` ADD INDEX `idx_activity_type` (`activity_type`);
ALTER TABLE `community_activity` ADD INDEX `idx_start_time` (`start_time`);
ALTER TABLE `community_activity` ADD INDEX `idx_create_time` (`create_time`);
ALTER TABLE `activity_registration` ADD INDEX `idx_activity_user` (`activity_id`, `user_id`);
ALTER TABLE `activity_registration` ADD INDEX `idx_status` (`status`);
ALTER TABLE `activity_registration` ADD INDEX `idx_register_time` (`register_time`);
ALTER TABLE `work_order` ADD INDEX `idx_title` (`title`);
ALTER TABLE `work_order` ADD INDEX `idx_order_type` (`order_type`);
ALTER TABLE `work_order` ADD INDEX `idx_status` (`status`);
ALTER TABLE `work_order` ADD INDEX `idx_submitter_handler` (`submitter_id`, `handler_id`);
ALTER TABLE `work_order` ADD INDEX `idx_create_time` (`create_time`);
ALTER TABLE `floating_population` ADD INDEX `idx_name` (`name`);
ALTER TABLE `floating_population` ADD INDEX `idx_id_card` (`id_card`);
ALTER TABLE `floating_population` ADD INDEX `idx_phone` (`phone`);
ALTER TABLE `floating_population` ADD INDEX `idx_current_address` (`current_address`);
ALTER TABLE `floating_population` ADD INDEX `idx_expected_leave_date` (`expected_leave_date`);
ALTER TABLE `floating_population` ADD INDEX `idx_create_time` (`create_time`);
ALTER TABLE `volunteer` ADD INDEX `idx_user_id` (`user_id`);
ALTER TABLE `volunteer` ADD INDEX `idx_phone` (`phone`);
ALTER TABLE `volunteer` ADD INDEX `idx_status` (`status`);
ALTER TABLE `volunteer` ADD INDEX `idx_join_date` (`join_date`);
ALTER TABLE `security_hazard` ADD INDEX `idx_title` (`title`);
ALTER TABLE `security_hazard` ADD INDEX `idx_hazard_type` (`hazard_type`);
ALTER TABLE `security_hazard` ADD INDEX `idx_location` (`location`);
ALTER TABLE `security_hazard` ADD INDEX `idx_status` (`status`);
ALTER TABLE `security_hazard` ADD INDEX `idx_reporter_handler` (`reporter_id`, `handler_id`);
ALTER TABLE `security_hazard` ADD INDEX `idx_create_time` (`create_time`);
ALTER TABLE `notice` ADD INDEX `idx_title` (`title`);
ALTER TABLE `notice` ADD INDEX `idx_notice_type` (`notice_type`);
ALTER TABLE `notice` ADD INDEX `idx_is_top` (`is_top`);
ALTER TABLE `notice` ADD INDEX `idx_publish_time` (`publish_time`);
ALTER TABLE `notice` ADD INDEX `idx_publisher` (`publisher_id`);
ALTER TABLE `notice_read_record` ADD INDEX `idx_notice_user` (`notice_id`, `user_id`);
ALTER TABLE `notice_read_record` ADD INDEX `idx_read_time` (`read_time`);
ALTER TABLE `neighbor_help` ADD INDEX `idx_title` (`title`);
ALTER TABLE `neighbor_help` ADD INDEX `idx_help_type` (`help_type`);
ALTER TABLE `neighbor_help` ADD INDEX `idx_category` (`category`);
ALTER TABLE `neighbor_help` ADD INDEX `idx_status` (`status`);
ALTER TABLE `neighbor_help` ADD INDEX `idx_user_id` (`user_id`);
ALTER TABLE `neighbor_help` ADD INDEX `idx_create_time` (`create_time`);
ALTER TABLE `service_appointment` ADD INDEX `idx_service_type` (`service_type`);
ALTER TABLE `service_appointment` ADD INDEX `idx_title` (`title`);
ALTER TABLE `service_appointment` ADD INDEX `idx_status` (`status`);
ALTER TABLE `service_appointment` ADD INDEX `idx_user_id` (`user_id`);
ALTER TABLE `service_appointment` ADD INDEX `idx_appointment_time` (`appointment_time`);
ALTER TABLE `service_appointment` ADD INDEX `idx_create_time` (`create_time`);
ALTER TABLE `operation_log` ADD INDEX `idx_user_id` (`user_id`);
ALTER TABLE `operation_log` ADD INDEX `idx_username` (`username`);
ALTER TABLE `operation_log` ADD INDEX `idx_operation` (`operation`);
ALTER TABLE `operation_log` ADD INDEX `idx_create_time` (`create_time`);
ALTER TABLE `family_relation` ADD INDEX `idx_resident_related` (`resident_id`, `related_resident_id`);
ALTER TABLE `points_account` ADD INDEX `idx_total_points` (`total_points`);
ALTER TABLE `points_record` ADD INDEX `idx_user_time` (`user_id`, `create_time`);
ALTER TABLE `points_record` ADD INDEX `idx_business` (`business_type`, `business_id`);
ALTER TABLE `message_notice` ADD INDEX `idx_user_read_time` (`user_id`, `is_read`, `create_time`);
ALTER TABLE `message_notice` ADD INDEX `idx_business` (`business_type`, `business_id`);

-- ----------------------------
-- RBAC permission data
-- ----------------------------
INSERT INTO `sys_permission` (
    `permission_code`, `permission_name`, `permission_type`, `module_key`, `parent_code`, `route_path`, `sort_no`, `status`, `remark`
) VALUES
    ('menu.home', '首页', 'MENU', 'home', NULL, '/home', 10, 1, '首页菜单'),
    ('menu.activity', '社区活动', 'MENU', 'activity', NULL, '/activity', 20, 1, '社区活动菜单'),
    ('menu.volunteer', '志愿者管理', 'MENU', 'volunteer', NULL, '/volunteer', 30, 1, '志愿者菜单'),
    ('menu.guide', '办事指南', 'MENU', 'guide', NULL, '/guide', 40, 1, '办事指南菜单'),
    ('menu.appointment', '服务预约', 'MENU', 'appointment', NULL, '/appointment', 50, 1, '服务预约菜单'),
    ('menu.neighborhelp', '邻里互助', 'MENU', 'neighborhelp', NULL, '/neighborhelp', 60, 1, '邻里互助菜单'),
    ('menu.points', '积分中心', 'MENU', 'points', NULL, '/points', 70, 1, '积分中心菜单'),
    ('menu.message', '消息中心', 'MENU', 'message', NULL, '/message', 80, 1, '消息中心菜单'),
    ('menu.evaluation', '服务评价', 'MENU', 'evaluation', NULL, '/evaluation', 90, 1, '服务评价菜单'),
    ('menu.workorder', '工单管理', 'MENU', 'workorder', NULL, '/workorder', 100, 1, '工单管理菜单'),
    ('menu.workorder.overtime', '工单超时看板', 'MENU', 'workorder', 'menu.workorder', '/workorder-overtime', 110, 1, '工单超时看板菜单'),
    ('menu.hazard', '治安隐患', 'MENU', 'hazard', NULL, '/hazard', 120, 1, '治安隐患菜单'),
    ('menu.notice', '通知公告', 'MENU', 'notice', NULL, '/notice', 130, 1, '通知公告菜单'),
    ('menu.resident', '居民管理', 'MENU', 'resident', NULL, '/resident', 140, 1, '居民管理菜单'),
    ('menu.floating', '流动人口', 'MENU', 'floating', NULL, '/floating', 150, 1, '流动人口菜单'),
    ('menu.dispatch.rule', '派单规则', 'MENU', 'dispatch_rule', NULL, '/dispatch-rule', 160, 1, '派单规则菜单'),
    ('menu.user', '用户管理', 'MENU', 'user', NULL, '/user', 170, 1, '用户管理菜单'),
    ('menu.log', '操作日志', 'MENU', 'log', NULL, '/log', 180, 1, '操作日志菜单'),
    ('menu.profile', '个人中心', 'MENU', 'profile', NULL, '/profile', 190, 1, '个人中心菜单'),
    ('btn.workorder.add', '提交工单', 'BUTTON', 'workorder', 'menu.workorder', NULL, 1001, 1, '工单新增'),
    ('btn.workorder.edit', '编辑工单', 'BUTTON', 'workorder', 'menu.workorder', NULL, 1002, 1, '工单编辑'),
    ('btn.workorder.delete', '删除工单', 'BUTTON', 'workorder', 'menu.workorder', NULL, 1003, 1, '工单删除'),
    ('btn.workorder.handle', '处理工单', 'BUTTON', 'workorder', 'menu.workorder', NULL, 1004, 1, '工单处理'),
    ('btn.workorder.export', '导出工单', 'BUTTON', 'workorder', 'menu.workorder', NULL, 1005, 1, '工单导出'),
    ('btn.volunteer.apply', '申请志愿者', 'BUTTON', 'volunteer', 'menu.volunteer', NULL, 1101, 1, '志愿者申请'),
    ('btn.volunteer.audit', '审核志愿者', 'BUTTON', 'volunteer', 'menu.volunteer', NULL, 1102, 1, '志愿者审核'),
    ('btn.volunteer.edit', '编辑志愿者', 'BUTTON', 'volunteer', 'menu.volunteer', NULL, 1103, 1, '志愿者编辑'),
    ('btn.volunteer.delete', '删除志愿者', 'BUTTON', 'volunteer', 'menu.volunteer', NULL, 1104, 1, '志愿者删除'),
    ('btn.user.add', '新增用户', 'BUTTON', 'user', 'menu.user', NULL, 1201, 1, '用户新增'),
    ('btn.user.edit', '编辑用户', 'BUTTON', 'user', 'menu.user', NULL, 1202, 1, '用户编辑'),
    ('btn.user.delete', '删除用户', 'BUTTON', 'user', 'menu.user', NULL, 1203, 1, '用户删除'),
    ('btn.dispatch.rule.add', '新增派单规则', 'BUTTON', 'dispatch_rule', 'menu.dispatch.rule', NULL, 1301, 1, '派单规则新增'),
    ('btn.dispatch.rule.edit', '编辑派单规则', 'BUTTON', 'dispatch_rule', 'menu.dispatch.rule', NULL, 1302, 1, '派单规则编辑'),
    ('btn.dispatch.rule.delete', '删除派单规则', 'BUTTON', 'dispatch_rule', 'menu.dispatch.rule', NULL, 1303, 1, '派单规则删除'),
    ('btn.dispatch.rule.preview', '预览派单规则', 'BUTTON', 'dispatch_rule', 'menu.dispatch.rule', NULL, 1304, 1, '派单规则预览'),
    ('btn.notice.add', '发布通知', 'BUTTON', 'notice', 'menu.notice', NULL, 1401, 1, '通知新增'),
    ('btn.notice.edit', '编辑通知', 'BUTTON', 'notice', 'menu.notice', NULL, 1402, 1, '通知编辑'),
    ('btn.notice.delete', '删除通知', 'BUTTON', 'notice', 'menu.notice', NULL, 1403, 1, '通知删除'),
    ('btn.notice.stats', '查看通知统计', 'BUTTON', 'notice', 'menu.notice', NULL, 1404, 1, '通知统计'),
    ('btn.resident.add', '新增居民', 'BUTTON', 'resident', 'menu.resident', NULL, 1501, 1, '居民新增'),
    ('btn.resident.edit', '编辑居民', 'BUTTON', 'resident', 'menu.resident', NULL, 1502, 1, '居民编辑'),
    ('btn.resident.delete', '删除居民', 'BUTTON', 'resident', 'menu.resident', NULL, 1503, 1, '居民删除'),
    ('btn.resident.export', '导出居民', 'BUTTON', 'resident', 'menu.resident', NULL, 1504, 1, '居民导出'),
    ('btn.resident.family.add', '新增家庭关系', 'BUTTON', 'resident', 'menu.resident', NULL, 1505, 1, '家庭关系新增'),
    ('btn.resident.family.delete', '删除家庭关系', 'BUTTON', 'resident', 'menu.resident', NULL, 1506, 1, '家庭关系删除'),
    ('btn.activity.add', '发布活动', 'BUTTON', 'activity', 'menu.activity', NULL, 1601, 1, '活动新增'),
    ('btn.activity.edit', '编辑活动', 'BUTTON', 'activity', 'menu.activity', NULL, 1602, 1, '活动编辑'),
    ('btn.activity.delete', '删除活动', 'BUTTON', 'activity', 'menu.activity', NULL, 1603, 1, '活动删除'),
    ('btn.guide.add', '发布指南', 'BUTTON', 'guide', 'menu.guide', NULL, 1701, 1, '指南新增'),
    ('btn.guide.edit', '编辑指南', 'BUTTON', 'guide', 'menu.guide', NULL, 1702, 1, '指南编辑'),
    ('btn.guide.delete', '删除指南', 'BUTTON', 'guide', 'menu.guide', NULL, 1703, 1, '指南删除'),
    ('btn.appointment.add', '新增预约', 'BUTTON', 'appointment', 'menu.appointment', NULL, 1801, 1, '预约新增'),
    ('btn.appointment.edit', '编辑预约', 'BUTTON', 'appointment', 'menu.appointment', NULL, 1802, 1, '预约编辑'),
    ('btn.appointment.delete', '删除预约', 'BUTTON', 'appointment', 'menu.appointment', NULL, 1803, 1, '预约删除'),
    ('btn.appointment.status.confirm', '确认预约', 'BUTTON', 'appointment', 'menu.appointment', NULL, 1804, 1, '预约确认'),
    ('btn.appointment.status.complete', '完成预约', 'BUTTON', 'appointment', 'menu.appointment', NULL, 1805, 1, '预约完成'),
    ('btn.appointment.status.cancel', '取消预约', 'BUTTON', 'appointment', 'menu.appointment', NULL, 1806, 1, '预约取消'),
    ('btn.neighborhelp.add', '发布互助', 'BUTTON', 'neighborhelp', 'menu.neighborhelp', NULL, 1901, 1, '互助新增'),
    ('btn.neighborhelp.edit', '编辑互助', 'BUTTON', 'neighborhelp', 'menu.neighborhelp', NULL, 1902, 1, '互助编辑'),
    ('btn.neighborhelp.delete', '删除互助', 'BUTTON', 'neighborhelp', 'menu.neighborhelp', NULL, 1903, 1, '互助删除'),
    ('btn.neighborhelp.status.complete', '完成互助', 'BUTTON', 'neighborhelp', 'menu.neighborhelp', NULL, 1904, 1, '互助完成'),
    ('btn.neighborhelp.status.close', '关闭互助', 'BUTTON', 'neighborhelp', 'menu.neighborhelp', NULL, 1905, 1, '互助关闭'),
    ('btn.message.read', '标记消息已读', 'BUTTON', 'message', 'menu.message', NULL, 2001, 1, '消息已读'),
    ('btn.message.read_all', '全部消息已读', 'BUTTON', 'message', 'menu.message', NULL, 2002, 1, '消息全部已读'),
    ('btn.message.admin_list', '查看消息管理列表', 'BUTTON', 'message', 'menu.message', NULL, 2003, 1, '消息管理列表'),
    ('btn.evaluation.submit', '提交评价', 'BUTTON', 'evaluation', 'menu.evaluation', NULL, 2101, 1, '评价提交'),
    ('btn.evaluation.admin_list', '查看评价管理列表', 'BUTTON', 'evaluation', 'menu.evaluation', NULL, 2102, 1, '评价管理列表'),
    ('btn.evaluation.stats', '查看评价统计', 'BUTTON', 'evaluation', 'menu.evaluation', NULL, 2103, 1, '评价统计'),
    ('btn.hazard.add', '上报隐患', 'BUTTON', 'hazard', 'menu.hazard', NULL, 2201, 1, '隐患新增'),
    ('btn.hazard.edit', '编辑隐患', 'BUTTON', 'hazard', 'menu.hazard', NULL, 2202, 1, '隐患编辑'),
    ('btn.hazard.delete', '删除隐患', 'BUTTON', 'hazard', 'menu.hazard', NULL, 2203, 1, '隐患删除'),
    ('btn.hazard.handle', '处理隐患', 'BUTTON', 'hazard', 'menu.hazard', NULL, 2204, 1, '隐患处理'),
    ('btn.floating.add', '登记流动人口', 'BUTTON', 'floating', 'menu.floating', NULL, 2301, 1, '流动人口新增'),
    ('btn.floating.edit', '编辑流动人口', 'BUTTON', 'floating', 'menu.floating', NULL, 2302, 1, '流动人口编辑'),
    ('btn.floating.delete', '删除流动人口', 'BUTTON', 'floating', 'menu.floating', NULL, 2303, 1, '流动人口删除'),
    ('btn.log.delete', '删除日志', 'BUTTON', 'log', 'menu.log', NULL, 2401, 1, '日志删除'),
    ('btn.log.clear', '清空日志', 'BUTTON', 'log', 'menu.log', NULL, 2402, 1, '日志清空'),
    ('btn.profile.update', '修改个人资料', 'BUTTON', 'profile', 'menu.profile', NULL, 2501, 1, '个人资料修改'),
    ('btn.profile.password', '修改个人密码', 'BUTTON', 'profile', 'menu.profile', NULL, 2502, 1, '个人密码修改'),
    ('scope.workorder.self', '工单仅本人', 'DATA_SCOPE', 'workorder', 'menu.workorder', NULL, 3001, 1, '仅查看本人提交的工单'),
    ('scope.workorder.all', '工单全量', 'DATA_SCOPE', 'workorder', 'menu.workorder', NULL, 3002, 1, '查看全部工单'),
    ('scope.appointment.self', '预约仅本人', 'DATA_SCOPE', 'appointment', 'menu.appointment', NULL, 3101, 1, '仅查看本人预约'),
    ('scope.appointment.all', '预约全量', 'DATA_SCOPE', 'appointment', 'menu.appointment', NULL, 3102, 1, '查看全部预约'),
    ('scope.message.self', '消息仅本人', 'DATA_SCOPE', 'message', 'menu.message', NULL, 3201, 1, '仅查看本人消息'),
    ('scope.message.all', '消息全量', 'DATA_SCOPE', 'message', 'menu.message', NULL, 3202, 1, '查看全部消息'),
    ('scope.evaluation.self', '评价仅本人', 'DATA_SCOPE', 'evaluation', 'menu.evaluation', NULL, 3301, 1, '仅查看本人评价'),
    ('scope.evaluation.all', '评价全量', 'DATA_SCOPE', 'evaluation', 'menu.evaluation', NULL, 3302, 1, '查看全部评价'),
    ('scope.volunteer.public_self', '志愿者公开加本人', 'DATA_SCOPE', 'volunteer', 'menu.volunteer', NULL, 3401, 1, '查看已通过志愿者与本人申请'),
    ('scope.volunteer.all', '志愿者全量', 'DATA_SCOPE', 'volunteer', 'menu.volunteer', NULL, 3402, 1, '查看全部志愿者记录'),
    ('scope.notice.published', '公告已发布可见', 'DATA_SCOPE', 'notice', 'menu.notice', NULL, 3501, 1, '仅查看已发布公告'),
    ('scope.notice.all', '公告全量', 'DATA_SCOPE', 'notice', 'menu.notice', NULL, 3502, 1, '查看全部公告');

INSERT INTO `sys_role_permission` (`role_code`, `permission_code`) VALUES
('ADMIN', 'menu.home'), ('ADMIN', 'menu.activity'), ('ADMIN', 'menu.volunteer'), ('ADMIN', 'menu.guide'),
('ADMIN', 'menu.appointment'), ('ADMIN', 'menu.neighborhelp'), ('ADMIN', 'menu.points'), ('ADMIN', 'menu.message'),
('ADMIN', 'menu.evaluation'), ('ADMIN', 'menu.workorder'), ('ADMIN', 'menu.workorder.overtime'), ('ADMIN', 'menu.hazard'),
('ADMIN', 'menu.notice'), ('ADMIN', 'menu.resident'), ('ADMIN', 'menu.floating'), ('ADMIN', 'menu.dispatch.rule'),
('ADMIN', 'menu.user'), ('ADMIN', 'menu.log'), ('ADMIN', 'menu.profile'),
('ADMIN', 'btn.workorder.add'), ('ADMIN', 'btn.workorder.edit'), ('ADMIN', 'btn.workorder.delete'),
('ADMIN', 'btn.workorder.handle'), ('ADMIN', 'btn.workorder.export'),
('ADMIN', 'btn.volunteer.apply'), ('ADMIN', 'btn.volunteer.audit'), ('ADMIN', 'btn.volunteer.edit'), ('ADMIN', 'btn.volunteer.delete'),
('ADMIN', 'btn.user.add'), ('ADMIN', 'btn.user.edit'), ('ADMIN', 'btn.user.delete'),
('ADMIN', 'btn.dispatch.rule.add'), ('ADMIN', 'btn.dispatch.rule.edit'), ('ADMIN', 'btn.dispatch.rule.delete'), ('ADMIN', 'btn.dispatch.rule.preview'),
('ADMIN', 'btn.notice.add'), ('ADMIN', 'btn.notice.edit'), ('ADMIN', 'btn.notice.delete'), ('ADMIN', 'btn.notice.stats'),
('ADMIN', 'btn.resident.add'), ('ADMIN', 'btn.resident.edit'), ('ADMIN', 'btn.resident.delete'), ('ADMIN', 'btn.resident.export'),
('ADMIN', 'btn.resident.family.add'), ('ADMIN', 'btn.resident.family.delete'),
('ADMIN', 'btn.activity.add'), ('ADMIN', 'btn.activity.edit'), ('ADMIN', 'btn.activity.delete'),
('ADMIN', 'btn.guide.add'), ('ADMIN', 'btn.guide.edit'), ('ADMIN', 'btn.guide.delete'),
('ADMIN', 'btn.appointment.add'), ('ADMIN', 'btn.appointment.edit'), ('ADMIN', 'btn.appointment.delete'),
('ADMIN', 'btn.appointment.status.confirm'), ('ADMIN', 'btn.appointment.status.complete'), ('ADMIN', 'btn.appointment.status.cancel'),
('ADMIN', 'btn.neighborhelp.add'), ('ADMIN', 'btn.neighborhelp.edit'), ('ADMIN', 'btn.neighborhelp.delete'),
('ADMIN', 'btn.neighborhelp.status.complete'), ('ADMIN', 'btn.neighborhelp.status.close'),
('ADMIN', 'btn.message.read'), ('ADMIN', 'btn.message.read_all'), ('ADMIN', 'btn.message.admin_list'),
('ADMIN', 'btn.evaluation.submit'), ('ADMIN', 'btn.evaluation.admin_list'), ('ADMIN', 'btn.evaluation.stats'),
('ADMIN', 'btn.hazard.add'), ('ADMIN', 'btn.hazard.edit'), ('ADMIN', 'btn.hazard.delete'), ('ADMIN', 'btn.hazard.handle'),
('ADMIN', 'btn.floating.add'), ('ADMIN', 'btn.floating.edit'), ('ADMIN', 'btn.floating.delete'),
('ADMIN', 'btn.log.delete'), ('ADMIN', 'btn.log.clear'),
('ADMIN', 'btn.profile.update'), ('ADMIN', 'btn.profile.password'),
('ADMIN', 'scope.workorder.self'), ('ADMIN', 'scope.workorder.all'),
('ADMIN', 'scope.appointment.self'), ('ADMIN', 'scope.appointment.all'),
('ADMIN', 'scope.message.self'), ('ADMIN', 'scope.message.all'),
('ADMIN', 'scope.evaluation.self'), ('ADMIN', 'scope.evaluation.all'),
('ADMIN', 'scope.volunteer.public_self'), ('ADMIN', 'scope.volunteer.all'),
('ADMIN', 'scope.notice.published'), ('ADMIN', 'scope.notice.all'),
('USER', 'menu.home'), ('USER', 'menu.activity'), ('USER', 'menu.volunteer'), ('USER', 'menu.guide'),
('USER', 'menu.appointment'), ('USER', 'menu.neighborhelp'), ('USER', 'menu.points'), ('USER', 'menu.message'),
('USER', 'menu.evaluation'), ('USER', 'menu.workorder'), ('USER', 'menu.hazard'), ('USER', 'menu.notice'), ('USER', 'menu.profile'),
('USER', 'btn.workorder.add'), ('USER', 'btn.workorder.edit'), ('USER', 'btn.workorder.delete'),
('USER', 'btn.volunteer.apply'), ('USER', 'btn.volunteer.edit'), ('USER', 'btn.volunteer.delete'),
('USER', 'btn.appointment.add'), ('USER', 'btn.appointment.edit'), ('USER', 'btn.appointment.delete'),
('USER', 'btn.neighborhelp.add'), ('USER', 'btn.neighborhelp.edit'), ('USER', 'btn.neighborhelp.delete'),
('USER', 'btn.message.read'), ('USER', 'btn.message.read_all'),
('USER', 'btn.evaluation.submit'),
('USER', 'btn.hazard.add'), ('USER', 'btn.hazard.edit'), ('USER', 'btn.hazard.delete'),
('USER', 'btn.profile.update'), ('USER', 'btn.profile.password'),
('USER', 'scope.workorder.self'), ('USER', 'scope.appointment.self'),
('USER', 'scope.message.self'), ('USER', 'scope.evaluation.self'),
('USER', 'scope.volunteer.public_self'), ('USER', 'scope.notice.published');

SET FOREIGN_KEY_CHECKS = 1;
