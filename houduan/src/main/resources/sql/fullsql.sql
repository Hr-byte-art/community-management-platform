/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : localhost:3306
 Source Schema         : community_service

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 16/03/2026 21:06:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity_registration
-- ----------------------------
DROP TABLE IF EXISTS `activity_registration`;
CREATE TABLE `activity_registration`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activity_id` bigint NOT NULL COMMENT '活动ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-已报名，1-已签到，2-已取消',
  `register_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `check_in_time` datetime NULL DEFAULT NULL COMMENT '签到时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动报名表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_registration
-- ----------------------------
INSERT INTO `activity_registration` VALUES (1, 1, 2, 0, '2025-01-05 10:00:00', NULL);
INSERT INTO `activity_registration` VALUES (2, 1, 3, 0, '2025-01-05 11:00:00', NULL);
INSERT INTO `activity_registration` VALUES (3, 1, 4, 0, '2025-01-06 09:00:00', NULL);
INSERT INTO `activity_registration` VALUES (4, 1, 5, 0, '2025-01-06 14:00:00', NULL);
INSERT INTO `activity_registration` VALUES (5, 2, 2, 0, '2025-01-08 14:00:00', NULL);
INSERT INTO `activity_registration` VALUES (6, 2, 5, 0, '2025-01-08 15:00:00', NULL);
INSERT INTO `activity_registration` VALUES (7, 3, 3, 1, '2025-01-10 08:00:00', '2025-01-15 08:05:00');
INSERT INTO `activity_registration` VALUES (8, 3, 4, 1, '2025-01-10 09:00:00', '2025-01-15 08:10:00');
INSERT INTO `activity_registration` VALUES (9, 4, 6, 1, '2025-01-08 10:00:00', '2025-01-10 13:55:00');
INSERT INTO `activity_registration` VALUES (10, 4, 8, 1, '2025-01-09 11:00:00', '2025-01-10 14:00:00');
INSERT INTO `activity_registration` VALUES (11, 5, 2, 0, '2025-01-12 09:00:00', NULL);
INSERT INTO `activity_registration` VALUES (12, 5, 3, 2, '2025-01-12 10:00:00', NULL);

-- ----------------------------
-- Table structure for community_activity
-- ----------------------------
DROP TABLE IF EXISTS `community_activity`;
CREATE TABLE `community_activity`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '活动标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '活动内容',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'https://tu.ltyuanfang.cn/api/fengjing.php' COMMENT '封面图片',
  `activity_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动类型：CULTURE-文化，SPORT-体育，VOLUNTEER-志愿，OTHER-其他',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动地点',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `is_cancelled` tinyint NULL DEFAULT 0 COMMENT '是否取消：0-正常，1-已取消',
  `organizer_id` bigint NULL DEFAULT NULL COMMENT '组织者ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社区活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of community_activity
-- ----------------------------
INSERT INTO `community_activity` VALUES (1, '春节联欢晚会', '社区居民春节联欢活动，包含文艺表演、抽奖等环节', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'CULTURE', '社区活动中心大厅', '2025-01-25 19:00:00', '2025-01-25 22:00:00', 0, 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `community_activity` VALUES (2, '社区篮球赛', '社区居民篮球友谊赛，欢迎篮球爱好者报名参加', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'SPORT', '社区篮球场', '2025-01-20 09:00:00', '2025-01-20 17:00:00', 0, 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `community_activity` VALUES (3, '志愿者清洁活动', '社区环境清洁志愿活动，共建美好家园', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'VOLUNTEER', '社区各公共区域', '2025-01-15 08:00:00', '2025-01-15 12:00:00', 0, 7, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `community_activity` VALUES (4, '老年人健康讲座', '邀请三甲医院专家为老年居民讲解健康养生知识', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'OTHER', '社区会议室', '2025-01-10 14:00:00', '2025-01-10 16:00:00', 0, 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `community_activity` VALUES (5, '亲子手工活动', '家长与孩子一起制作手工艺品，增进亲子关系', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'CULTURE', '社区儿童活动室', '2025-01-18 10:00:00', '2025-01-18 12:00:00', 0, 7, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `community_activity` VALUES (6, '广场舞比赛', '社区广场舞队伍展示比赛，设有最佳表演奖', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'SPORT', '社区广场', '2025-01-22 18:00:00', '2025-01-22 20:00:00', 0, 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `community_activity` VALUES (7, '垃圾分类宣传活动', '普及垃圾分类知识，参与者可获得环保袋', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'VOLUNTEER', '社区广场', '2025-01-08 09:00:00', '2025-01-08 11:00:00', 0, 7, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `community_activity` VALUES (8, '元宵节猜灯谜', '元宵佳节猜灯谜活动，猜中有奖', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'CULTURE', '社区广场', '2025-02-12 18:00:00', '2025-02-12 20:00:00', 0, 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `community_activity` VALUES (9, '已取消的音乐会', '因天气原因取消的户外音乐会', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'CULTURE', '社区广场', '2025-01-05 19:00:00', '2025-01-05 21:00:00', 1, 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `community_activity` VALUES (10, '社区义诊活动', '邀请医院专家为居民免费体检', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'OTHER', '社区服务中心', '2025-01-30 08:00:00', '2025-01-30 17:00:00', 0, 7, '2026-03-12 22:19:30', '2026-03-12 22:19:30');

-- ----------------------------
-- Table structure for family_relation
-- ----------------------------
DROP TABLE IF EXISTS `family_relation`;
CREATE TABLE `family_relation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resident_id` bigint NOT NULL COMMENT '居民ID',
  `related_resident_id` bigint NOT NULL COMMENT '关联居民ID',
  `relation` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关系：SPOUSE-配偶，PARENT-父母，CHILD-子女，SIBLING-兄弟姐妹',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '家庭关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of family_relation
-- ----------------------------
INSERT INTO `family_relation` VALUES (1, 1, 2, 'SPOUSE', '2026-03-12 22:19:30');
INSERT INTO `family_relation` VALUES (2, 2, 1, 'SPOUSE', '2026-03-12 22:19:30');
INSERT INTO `family_relation` VALUES (3, 3, 4, 'PARENT', '2026-03-12 22:19:30');
INSERT INTO `family_relation` VALUES (4, 4, 3, 'CHILD', '2026-03-12 22:19:30');
INSERT INTO `family_relation` VALUES (5, 5, 6, 'SIBLING', '2026-03-12 22:19:30');
INSERT INTO `family_relation` VALUES (6, 6, 5, 'SIBLING', '2026-03-12 22:19:30');
INSERT INTO `family_relation` VALUES (7, 7, 8, 'SPOUSE', '2026-03-12 22:19:30');
INSERT INTO `family_relation` VALUES (8, 8, 7, 'SPOUSE', '2026-03-12 22:19:30');

-- ----------------------------
-- Table structure for floating_population
-- ----------------------------
DROP TABLE IF EXISTS `floating_population`;
CREATE TABLE `floating_population`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别：0-女，1-男',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `origin_place` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '户籍所在地',
  `current_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '现居住地址',
  `work_unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工作单位',
  `register_date` date NULL DEFAULT NULL COMMENT '登记日期',
  `expected_leave_date` date NULL DEFAULT NULL COMMENT '预计离开日期',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-已离开，1-在住',
  `photo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'https://tu.ltyuanfang.cn/api/fengjing.php' COMMENT '照片',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '流动人口表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of floating_population
-- ----------------------------
INSERT INTO `floating_population` VALUES (1, '李明', '320101199505051234', 1, '13700000001', '江苏省南京市', '幸福小区1号楼1单元103室', '某科技公司', '2024-06-01', '2025-06-01', 1, 'https://tu.ltyuanfang.cn/api/fengjing.php', '程序员', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `floating_population` VALUES (2, '王芳', '330101199208082345', 0, '13700000002', '浙江省杭州市', '幸福小区2号楼1单元201室', '某电商公司', '2024-03-15', '2025-03-15', 1, 'https://tu.ltyuanfang.cn/api/fengjing.php', '运营专员', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `floating_population` VALUES (3, '张伟', '410101198812123456', 1, '13700000003', '河南省郑州市', '幸福小区3号楼2单元301室', '某建筑公司', '2024-09-01', '2025-03-01', 1, 'https://tu.ltyuanfang.cn/api/fengjing.php', '工程师', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `floating_population` VALUES (4, '刘洋', '510101199703034567', 1, '13700000004', '四川省成都市', '幸福小区4号楼1单元401室', '某餐饮公司', '2024-01-10', '2024-12-31', 0, 'https://tu.ltyuanfang.cn/api/fengjing.php', '已离开', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `floating_population` VALUES (5, '陈静', '420101199606065678', 0, '13700000005', '湖北省武汉市', '幸福小区5号楼3单元501室', '某医院', '2024-07-20', '2025-07-20', 1, 'https://tu.ltyuanfang.cn/api/fengjing.php', '护士', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `floating_population` VALUES (6, '赵强', '370101199404046789', 1, '13700000006', '山东省济南市', '幸福小区1号楼2单元102室', '某物流公司', '2024-11-01', '2025-11-01', 1, 'https://tu.ltyuanfang.cn/api/fengjing.php', '快递员', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `floating_population` VALUES (7, '孙丽', '340101199809087890', 0, '13700000007', '安徽省合肥市', '幸福小区2号楼3单元202室', '某教育机构', '2024-08-15', '2025-08-15', 1, 'https://tu.ltyuanfang.cn/api/fengjing.php', '教师', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `floating_population` VALUES (8, '周杰', '610101199201018901', 1, '13700000008', '陕西省西安市', '幸福小区3号楼1单元302室', '自由职业', '2024-05-01', '2025-05-01', 1, 'https://tu.ltyuanfang.cn/api/fengjing.php', '设计师', '2026-03-12 22:19:30', '2026-03-12 22:19:30');

-- ----------------------------
-- Table structure for grid_dispatch_rule
-- ----------------------------
DROP TABLE IF EXISTS `grid_dispatch_rule`;
CREATE TABLE `grid_dispatch_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `grid_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '网格名称/规则名称',
  `order_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工单类型，NULL表示通用',
  `priority` tinyint NULL DEFAULT NULL COMMENT '优先级，NULL表示通用',
  `assignee_id` bigint NOT NULL COMMENT '自动派单责任人ID',
  `enabled` tinyint NOT NULL DEFAULT 1 COMMENT '是否启用：0-否，1-是',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dispatch_order_type_priority`(`order_type` ASC, `priority` ASC) USING BTREE,
  INDEX `idx_dispatch_enabled`(`enabled` ASC) USING BTREE,
  INDEX `idx_dispatch_assignee`(`assignee_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '网格化自动派单规则表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of grid_dispatch_rule
-- ----------------------------
INSERT INTO `grid_dispatch_rule` VALUES (1, '默认报修派单规则', 'REPAIR', NULL, 1, 1, '优先派给管理员ID=1', '2026-03-12 22:20:10', '2026-03-12 22:20:10');

-- ----------------------------
-- Table structure for message_notice
-- ----------------------------
DROP TABLE IF EXISTS `message_notice`;
CREATE TABLE `message_notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息内容',
  `message_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息类型',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型',
  `business_id` bigint NULL DEFAULT NULL COMMENT '业务ID',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '已读时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '站内消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message_notice
-- ----------------------------
INSERT INTO `message_notice` VALUES (1, 1, '有新的工单待处理', '系统已为您自动派单：[3 栋 5 楼照明故障报修]，请及时处理。', 'SYSTEM', 'WORK_ORDER', 49, 0, NULL, '2026-03-14 03:34:32');

-- ----------------------------
-- Table structure for neighbor_help
-- ----------------------------
DROP TABLE IF EXISTS `neighbor_help`;
CREATE TABLE `neighbor_help`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
  `help_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类型：SEEK-求助，OFFER-提供帮助',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类：DAILY-日常，SKILL-技能，ITEM-物品，OTHER-其他',
  `user_id` bigint NULL DEFAULT NULL COMMENT '发布人ID',
  `contact_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系方式',
  `images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-已关闭，1-进行中，2-已完成',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '邻里互助表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of neighbor_help
-- ----------------------------
INSERT INTO `neighbor_help` VALUES (1, '求助：需要人帮忙照看小孩', '周末临时有事外出，需要邻居帮忙照看小孩2小时', 'SEEK', 'DAILY', 2, '13800000002', NULL, 1, 45, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `neighbor_help` VALUES (2, '提供：免费辅导小学数学', '退休教师，可免费辅导小学生数学', 'OFFER', 'SKILL', 3, '13800000003', NULL, 1, 89, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `neighbor_help` VALUES (3, '求助：借用电钻', '装修需要借用电钻一天，用完立即归还', 'SEEK', 'ITEM', 4, '13800000004', NULL, 2, 23, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `neighbor_help` VALUES (4, '提供：闲置婴儿车转让', '九成新婴儿车，低价转让200元', 'OFFER', 'ITEM', 5, '13800000005', 'https://tu.ltyuanfang.cn/api/fengjing.php', 1, 67, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `neighbor_help` VALUES (5, '求助：寻找走失的猫咪', '橘色猫咪走失，名叫橘子，如有发现请联系', 'SEEK', 'OTHER', 6, '13800000006', 'https://tu.ltyuanfang.cn/api/fengjing.php', 1, 156, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `neighbor_help` VALUES (6, '提供：代收快递服务', '白天在家，可帮邻居代收快递', 'OFFER', 'DAILY', 8, '13800000008', NULL, 1, 34, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `neighbor_help` VALUES (7, '求助：需要搬家帮手', '下周六搬家需要2-3人帮忙搬重物', 'SEEK', 'DAILY', 2, '13800000002', NULL, 0, 28, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `neighbor_help` VALUES (8, '提供：旧书籍免费赠送', '整理出一批旧书，免费赠送给需要的邻居', 'OFFER', 'ITEM', 3, '13800000003', 'https://tu.ltyuanfang.cn/api/fengjing.php', 1, 78, '2026-03-12 22:19:30', '2026-03-12 22:19:30');

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
  `notice_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类型：NOTICE-通知，ANNOUNCEMENT-公告，NEWS-新闻',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'https://tu.ltyuanfang.cn/api/fengjing.php' COMMENT '封面图片',
  `is_top` tinyint NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
  `publisher_id` bigint NULL DEFAULT NULL COMMENT '发布人ID',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-草稿，1-已发布，2-已下架',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '关于春节期间物业服务安排的通知', '春节期间（1月28日-2月4日）物业服务时间调整为9:00-17:00，紧急事务请拨打24小时值班电话。', 'NOTICE', 'https://tu.ltyuanfang.cn/api/fengjing.php', 1, 1, 523, 1, '2025-01-10 09:00:00', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `notice` VALUES (2, '社区2025年工作计划公告', '2025年社区将重点推进智慧社区建设、老旧设施改造等工作。', 'ANNOUNCEMENT', 'https://tu.ltyuanfang.cn/api/fengjing.php', 0, 1, 234, 1, '2025-01-05 10:00:00', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `notice` VALUES (3, '停水通知', '因管道维修，1月15日8:00-18:00，1-3号楼将停水，请提前做好储水准备。', 'NOTICE', 'https://tu.ltyuanfang.cn/api/fengjing.php', 1, 7, 456, 1, '2025-01-12 14:00:00', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `notice` VALUES (4, '社区志愿者招募公告', '社区现招募志愿者，有意者请到社区服务中心报名。', 'ANNOUNCEMENT', 'https://tu.ltyuanfang.cn/api/fengjing.php', 0, 1, 189, 1, '2025-01-08 11:00:00', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `notice` VALUES (5, '电梯维保通知', '1月20日将对各楼栋电梯进行例行维保，届时电梯将暂停使用2小时。', 'NOTICE', 'https://tu.ltyuanfang.cn/api/fengjing.php', 0, 7, 312, 1, '2025-01-11 15:00:00', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `notice` VALUES (6, '社区文艺汇演圆满结束', '2025年社区迎新文艺汇演于1月8日成功举办，感谢各位居民的参与。', 'NEWS', 'https://tu.ltyuanfang.cn/api/fengjing.php', 0, 1, 278, 1, '2025-01-09 16:00:00', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `notice` VALUES (7, '垃圾分类宣传', '请各位居民按照规定进行垃圾分类，共建美好家园。', 'NOTICE', 'https://tu.ltyuanfang.cn/api/fengjing.php', 0, 1, 145, 1, '2025-01-06 09:00:00', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `notice` VALUES (8, '消防安全提醒', '冬季干燥，请注意用火用电安全，不要在楼道堆放杂物。', 'NOTICE', 'https://tu.ltyuanfang.cn/api/fengjing.php', 0, 7, 198, 1, '2025-01-07 10:00:00', '2026-03-12 22:19:30', '2026-03-12 22:19:30');

-- ----------------------------
-- Table structure for notice_read_record
-- ----------------------------
DROP TABLE IF EXISTS `notice_read_record`;
CREATE TABLE `notice_read_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `notice_id` bigint NOT NULL COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `read_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知阅读记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice_read_record
-- ----------------------------
INSERT INTO `notice_read_record` VALUES (1, 1, 2, '2025-01-10 10:00:00');
INSERT INTO `notice_read_record` VALUES (2, 1, 3, '2025-01-10 11:00:00');
INSERT INTO `notice_read_record` VALUES (3, 1, 4, '2025-01-10 12:00:00');
INSERT INTO `notice_read_record` VALUES (4, 1, 5, '2025-01-10 13:00:00');
INSERT INTO `notice_read_record` VALUES (5, 1, 6, '2025-01-10 14:00:00');
INSERT INTO `notice_read_record` VALUES (6, 2, 2, '2025-01-05 14:00:00');
INSERT INTO `notice_read_record` VALUES (7, 2, 3, '2025-01-06 09:00:00');
INSERT INTO `notice_read_record` VALUES (8, 3, 2, '2025-01-12 15:00:00');
INSERT INTO `notice_read_record` VALUES (9, 3, 3, '2025-01-12 16:00:00');
INSERT INTO `notice_read_record` VALUES (10, 3, 4, '2025-01-12 17:00:00');
INSERT INTO `notice_read_record` VALUES (11, 4, 5, '2025-01-08 14:00:00');
INSERT INTO `notice_read_record` VALUES (12, 4, 6, '2025-01-09 10:00:00');
INSERT INTO `notice_read_record` VALUES (13, 5, 2, '2025-01-11 16:00:00');
INSERT INTO `notice_read_record` VALUES (14, 5, 3, '2025-01-11 17:00:00');
INSERT INTO `notice_read_record` VALUES (15, 6, 2, '2025-01-09 17:00:00');
INSERT INTO `notice_read_record` VALUES (16, 6, 4, '2025-01-09 18:00:00');

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `operation` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作描述',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '方法名',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '参数',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `time` bigint NULL DEFAULT NULL COMMENT '执行时长(毫秒)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES (1, 1, 'admin', '用户登录', 'com.community.controller.AuthController.login', NULL, '192.168.1.100', 45, '2025-01-10 08:30:00');
INSERT INTO `operation_log` VALUES (2, 1, 'admin', '查询居民列表', 'com.community.controller.ResidentController.list', NULL, '192.168.1.100', 32, '2025-01-10 08:35:00');
INSERT INTO `operation_log` VALUES (3, 1, 'admin', '新增居民信息', 'com.community.controller.ResidentController.add', NULL, '192.168.1.100', 28, '2025-01-10 09:00:00');
INSERT INTO `operation_log` VALUES (4, 2, 'zhangsan', '用户登录', 'com.community.controller.AuthController.login', NULL, '192.168.1.101', 38, '2025-01-10 09:15:00');
INSERT INTO `operation_log` VALUES (5, 2, 'zhangsan', '查询活动列表', 'com.community.controller.ActivityController.list', NULL, '192.168.1.101', 25, '2025-01-10 09:20:00');
INSERT INTO `operation_log` VALUES (6, 1, 'admin', '处理工单', 'com.community.controller.WorkOrderController.handle', NULL, '192.168.1.100', 56, '2025-01-10 10:00:00');
INSERT INTO `operation_log` VALUES (7, 3, 'lisi', '用户登录', 'com.community.controller.AuthController.login', NULL, '192.168.1.102', 35, '2025-01-10 10:30:00');
INSERT INTO `operation_log` VALUES (8, 3, 'lisi', '提交工单', 'com.community.controller.WorkOrderController.add', NULL, '192.168.1.102', 48, '2025-01-10 10:35:00');
INSERT INTO `operation_log` VALUES (9, 7, 'zhouba', '用户登录', 'com.community.controller.AuthController.login', NULL, '192.168.1.103', 40, '2025-01-10 11:00:00');
INSERT INTO `operation_log` VALUES (10, 7, 'zhouba', '发布通知公告', 'com.community.controller.NoticeController.add', NULL, '192.168.1.103', 52, '2025-01-10 11:15:00');
INSERT INTO `operation_log` VALUES (11, 1, 'admin', '审核志愿者', 'com.community.controller.VolunteerController.audit', NULL, '192.168.1.100', 30, '2025-01-10 14:00:00');
INSERT INTO `operation_log` VALUES (12, NULL, NULL, '用户登录', 'com.community.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 1716, NULL);
INSERT INTO `operation_log` VALUES (13, NULL, NULL, '用户登录', 'com.community.controller.AuthController.login', '[{\"username\":\"zhangsan\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 108, NULL);
INSERT INTO `operation_log` VALUES (14, 2, 'zhangsan', '申请成为志愿者', 'com.community.controller.VolunteerController.apply', '[{\"name\":\"张三\",\"phone\":\"17308006759\",\"skills\":\"唱跳rap打篮球\"},{}]', '0:0:0:0:0:0:0:1', 9, NULL);
INSERT INTO `operation_log` VALUES (15, 2, 'zhangsan', '编辑志愿者信息', 'com.community.controller.VolunteerController.update', '[1,{\"id\":1,\"userId\":2,\"name\":\"张三\",\"phone\":\"13800000002\",\"skills\":\"电脑维修、文案写作\",\"serviceHours\":50.5,\"status\":1,\"joinDate\":1682870400000,\"photo\":\"https://tu.ltyuanfang.cn/api/fengjing.php\",\"createTime\":1773325170000,\"updateTime\":1773325170000},{}]', '0:0:0:0:0:0:0:1', 7, NULL);
INSERT INTO `operation_log` VALUES (16, NULL, NULL, '用户登录', 'com.community.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 487, NULL);
INSERT INTO `operation_log` VALUES (17, NULL, NULL, '用户登录', 'com.community.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 485, NULL);
INSERT INTO `operation_log` VALUES (18, NULL, NULL, '用户登录', 'com.community.controller.AuthController.login', '[{\"username\":\"admin\",\"password\":\"123456\"}]', '0:0:0:0:0:0:0:1', 13, NULL);
INSERT INTO `operation_log` VALUES (19, 1, 'admin', 'Submit Work Order', 'com.community.controller.WorkOrderController.add', '[{\"id\":49,\"title\":\"3 栋 5 楼照明故障报修\",\"content\":\"3 栋 5 楼区域照明设施故障，影响居民夜间通行。存在老人跌倒安全隐患，需尽快处理。[请补充具体位置及联系人电话]\",\"orderType\":\"REPAIR\",\"submitterId\":1,\"assigneeId\":1,\"isOvertime\":0,\"status\":0,\"priority\":2},{}]', '0:0:0:0:0:0:0:1', 173, NULL);

-- ----------------------------
-- Table structure for points_account
-- ----------------------------
DROP TABLE IF EXISTS `points_account`;
CREATE TABLE `points_account`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_points` int NOT NULL DEFAULT 0 COMMENT '总积分',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '积分账户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_account
-- ----------------------------
INSERT INTO `points_account` VALUES (1, 1, 0, '2026-03-14 00:24:12', '2026-03-14 00:24:12');
INSERT INTO `points_account` VALUES (2, 2, 0, '2026-03-14 00:26:12', '2026-03-14 00:26:12');

-- ----------------------------
-- Table structure for points_record
-- ----------------------------
DROP TABLE IF EXISTS `points_record`;
CREATE TABLE `points_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `change_type` tinyint NOT NULL COMMENT '变动类型：1-增加，-1-扣减',
  `points` int NOT NULL COMMENT '变动积分值(正数)',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务类型',
  `business_id` bigint NOT NULL COMMENT '业务ID',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_unique_event`(`user_id` ASC, `business_type` ASC, `business_id` ASC, `change_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '积分流水表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of points_record
-- ----------------------------

-- ----------------------------
-- Table structure for resident
-- ----------------------------
DROP TABLE IF EXISTS `resident`;
CREATE TABLE `resident`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别：0-女，1-男',
  `birth_date` date NULL DEFAULT NULL COMMENT '出生日期',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '居住地址',
  `building_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '楼栋号',
  `unit_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单元号',
  `room_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '房间号',
  `residence_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '居住类型：OWN-自有，RENT-租住',
  `move_in_date` date NULL DEFAULT NULL COMMENT '入住日期',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-已迁出，1-在住',
  `photo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'https://tu.ltyuanfang.cn/api/fengjing.php' COMMENT '照片',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '居民信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of resident
-- ----------------------------
INSERT INTO `resident` VALUES (1, '陈晓明', '110101199001011234', 1, '1990-01-01', '13900000001', '幸福小区1号楼1单元101室', '1', '1', '101', 'OWN', '2018-05-15', 1, 'https://tu.ltyuanfang.cn/api/fengjing.php', '业主委员会成员', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `resident` VALUES (2, '王丽华', '110101198512152345', 0, '1985-12-15', '13900000002', '幸福小区1号楼1单元102室', '1', '1', '102', 'OWN', '2019-03-20', 1, 'https://tu.ltyuanfang.cn/api/fengjing.php', '业主', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `resident` VALUES (3, '李建国', '110101197803203456', 1, '1978-03-20', '13900000003', '幸福小区2号楼2单元201室', '2', '2', '201', 'OWN', '2017-08-10', 1, 'https://tu.ltyuanfang.cn/api/fengjing.php', '退休教师', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `resident` VALUES (4, '张美玲', '110101200005054567', 0, '2000-05-05', '13900000004', '幸福小区2号楼2单元202室', '2', '2', '202', 'RENT', '2023-01-01', 1, 'https://tu.ltyuanfang.cn/api/fengjing.php', '租户', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `resident` VALUES (5, '刘德华', '110101196808085678', 1, '1968-08-08', '13900000005', '幸福小区3号楼1单元301室', '3', '1', '301', 'OWN', '2015-06-18', 1, 'https://tu.ltyuanfang.cn/api/fengjing.php', '小区志愿者', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `resident` VALUES (6, '赵小燕', '110101199206126789', 0, '1992-06-12', '13900000006', '幸福小区3号楼1单元302室', '3', '1', '302', 'RENT', '2024-02-28', 1, 'https://tu.ltyuanfang.cn/api/fengjing.php', '租户', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `resident` VALUES (7, '孙伟强', '110101198811117890', 1, '1988-11-11', '13900000007', '幸福小区4号楼3单元401室', '4', '3', '401', 'OWN', '2020-09-01', 1, 'https://tu.ltyuanfang.cn/api/fengjing.php', '党员', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `resident` VALUES (8, '周雪梅', '110101197502028901', 0, '1975-02-02', '13900000008', '幸福小区4号楼3单元402室', '4', '3', '402', 'OWN', '2016-12-25', 1, 'https://tu.ltyuanfang.cn/api/fengjing.php', '楼栋长', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `resident` VALUES (9, '吴志远', '110101200108089012', 1, '2001-08-08', '13900000009', '幸福小区5号楼2单元501室', '5', '2', '501', 'RENT', '2025-01-10', 1, 'https://tu.ltyuanfang.cn/api/fengjing.php', '大学生租户', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `resident` VALUES (10, '郑秀英', '110101195509090123', 0, '1955-09-09', '13900000010', '幸福小区5号楼2单元502室', '5', '2', '502', 'OWN', '2010-04-01', 1, 'https://tu.ltyuanfang.cn/api/fengjing.php', '退休干部', '2026-03-12 22:19:30', '2026-03-12 22:19:30');

-- ----------------------------
-- Table structure for security_hazard
-- ----------------------------
DROP TABLE IF EXISTS `security_hazard`;
CREATE TABLE `security_hazard`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '隐患标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '隐患描述',
  `hazard_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '隐患类型：FIRE-消防，THEFT-盗窃，TRAFFIC-交通，OTHER-其他',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '隐患位置',
  `images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片',
  `reporter_id` bigint NULL DEFAULT NULL COMMENT '上报人ID',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-待处理，1-处理中，2-已解决，3-已关闭',
  `handle_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '处理结果',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '治安隐患表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of security_hazard
-- ----------------------------
INSERT INTO `security_hazard` VALUES (1, '消防通道堆放杂物', '1号楼1单元消防通道被大量杂物堵塞', 'FIRE', '幸福小区1号楼1单元', 'https://tu.ltyuanfang.cn/api/fengjing.php', 2, 1, 2, '已清理完毕', '2025-01-05 14:00:00', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `security_hazard` VALUES (2, '地下车库照明故障', '地下车库B区多处照明灯损坏', 'OTHER', '幸福小区地下车库B区', 'https://tu.ltyuanfang.cn/api/fengjing.php', 3, 1, 1, NULL, NULL, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `security_hazard` VALUES (3, '东门门禁损坏', '东门门禁系统故障，无法正常刷卡', 'THEFT', '幸福小区东门', 'https://tu.ltyuanfang.cn/api/fengjing.php', 4, NULL, 0, NULL, NULL, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `security_hazard` VALUES (4, '电动车违规充电', '3号楼有居民在楼道内给电动车充电', 'FIRE', '幸福小区3号楼2单元', 'https://tu.ltyuanfang.cn/api/fengjing.php', 5, 7, 2, '已劝导居民移至充电桩', '2025-01-08 10:00:00', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `security_hazard` VALUES (5, '小区内车辆超速', '有车辆在小区内超速行驶', 'TRAFFIC', '幸福小区内部道路', 'https://tu.ltyuanfang.cn/api/fengjing.php', 6, 1, 1, NULL, NULL, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `security_hazard` VALUES (6, '围墙破损', '小区北侧围墙有一处破损', 'THEFT', '幸福小区北侧围墙', 'https://tu.ltyuanfang.cn/api/fengjing.php', 2, 7, 2, '已修复围墙', '2025-01-02 16:00:00', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `security_hazard` VALUES (7, '灭火器过期', '5号楼各楼层灭火器已超过有效期', 'FIRE', '幸福小区5号楼', 'https://tu.ltyuanfang.cn/api/fengjing.php', 8, NULL, 0, NULL, NULL, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `security_hazard` VALUES (8, '儿童游乐设施松动', '儿童游乐区秋千座椅螺丝松动', 'OTHER', '幸福小区儿童游乐区', 'https://tu.ltyuanfang.cn/api/fengjing.php', 3, 1, 2, '已更换螺丝并加固', '2025-01-10 09:30:00', '2026-03-12 22:19:30', '2026-03-12 22:19:30');

-- ----------------------------
-- Table structure for service_appointment
-- ----------------------------
DROP TABLE IF EXISTS `service_appointment`;
CREATE TABLE `service_appointment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `service_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务类型：REPAIR-维修，CLEAN-保洁，MEDICAL-医疗，OTHER-其他',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '预约标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '预约内容',
  `user_id` bigint NULL DEFAULT NULL COMMENT '预约人ID',
  `appointment_time` datetime NULL DEFAULT NULL COMMENT '预约时间',
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务地址',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-待确认，1-已确认，2-已完成，3-已取消',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '服务预约表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_appointment
-- ----------------------------
INSERT INTO `service_appointment` VALUES (1, 'REPAIR', '水管漏水维修', '厨房水管接口处漏水，需要更换密封圈', 2, '2025-01-12 09:00:00', '张三', '13800000002', '幸福小区1号楼1单元101室', 1, '已安排维修师傅上门', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `service_appointment` VALUES (2, 'CLEAN', '家庭深度保洁', '春节前家庭大扫除，约120平米', 3, '2025-01-15 14:00:00', '李四', '13800000003', '幸福小区2号楼2单元201室', 0, '需要擦玻璃', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `service_appointment` VALUES (3, 'MEDICAL', '上门量血压', '老人行动不便，需要医护人员上门', 4, '2025-01-11 10:00:00', '王五', '13800000004', '幸福小区3号楼1单元301室', 2, '已完成，血压正常', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `service_appointment` VALUES (4, 'REPAIR', '空调不制热维修', '客厅空调开机后不制热', 5, '2025-01-13 15:00:00', '赵六', '13800000005', '幸福小区4号楼3单元401室', 1, '需要检查制冷剂', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `service_appointment` VALUES (5, 'OTHER', '开锁服务', '钥匙丢失，需要开锁服务', 6, '2025-01-10 18:30:00', '孙七', '13800000006', '幸福小区5号楼2单元501室', 2, '已开锁并更换锁芯', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `service_appointment` VALUES (6, 'CLEAN', '油烟机清洗', '油烟机使用多年，油污严重', 2, '2025-01-18 10:00:00', '张三', '13800000002', '幸福小区1号楼1单元101室', 0, NULL, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `service_appointment` VALUES (7, 'MEDICAL', '慢性病随访', '糖尿病患者定期随访', 8, '2025-01-14 09:30:00', '吴九', '13800000008', '幸福小区4号楼3单元402室', 1, '社区医生已确认', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `service_appointment` VALUES (8, 'REPAIR', '马桶堵塞疏通', '卫生间马桶堵塞，冲水不畅', 3, '2025-01-11 11:00:00', '李四', '13800000003', '幸福小区2号楼2单元201室', 3, '用户自行解决，取消预约', '2026-03-12 22:19:30', '2026-03-12 22:19:30');

-- ----------------------------
-- Table structure for service_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `service_evaluation`;
CREATE TABLE `service_evaluation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `work_order_id` bigint NOT NULL COMMENT '工单ID',
  `user_id` bigint NOT NULL COMMENT '评价用户ID（工单提交人）',
  `score` tinyint NOT NULL COMMENT '评分(1-5)',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '评价内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_work_order_user`(`work_order_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_eval_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_eval_score`(`score` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '工单服务评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_evaluation
-- ----------------------------

-- ----------------------------
-- Table structure for service_guide
-- ----------------------------
DROP TABLE IF EXISTS `service_guide`;
CREATE TABLE `service_guide`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类：CERTIFICATE-证件办理，SOCIAL-社保，HOUSING-住房，OTHER-其他',
  `required_materials` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '所需材料',
  `process_steps` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '办理流程',
  `contact_info` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系方式',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '办事指南表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of service_guide
-- ----------------------------
INSERT INTO `service_guide` VALUES (1, '居住证办理指南', '外来人员在本社区居住满半年可申请办理居住证', 'CERTIFICATE', '1.身份证原件及复印件\n2.房屋租赁合同\n3.近期1寸白底照片2张', '1.准备所需材料\n2.到社区服务中心领取申请表\n3.填写申请表并提交材料\n4.等待审核', '社区服务中心：010-12345678', 258, 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `service_guide` VALUES (2, '医保报销流程', '城镇居民医疗保险报销办理流程', 'SOCIAL', '1.医保卡\n2.身份证\n3.医院收费票据原件\n4.费用明细清单', '1.出院时在医院医保窗口办理\n2.或携带材料到社保中心办理', '社保服务热线：12333', 189, 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `service_guide` VALUES (3, '老年优待证办理', '60周岁以上老年人可申请办理老年优待证', 'CERTIFICATE', '1.身份证原件及复印件\n2.近期1寸彩色照片1张\n3.户口本', '1.携带材料到社区服务中心\n2.填写申请表\n3.现场审核通过即可领取', '社区服务中心：010-12345678', 145, 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `service_guide` VALUES (4, '公租房申请指南', '符合条件的居民可申请公共租赁住房', 'HOUSING', '1.身份证、户口本\n2.收入证明\n3.住房情况证明', '1.到社区领取并填写申请表\n2.提交材料进行初审\n3.街道办复审\n4.区住建局终审', '住房保障热线：010-87654321', 312, 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `service_guide` VALUES (5, '生育登记服务', '计划生育登记及相关证明办理', 'CERTIFICATE', '1.夫妻双方身份证\n2.结婚证\n3.户口本', '1.网上预约或现场取号\n2.提交材料\n3.当场办结', '计生服务电话：010-11112222', 98, 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `service_guide` VALUES (6, '养老保险查询', '城镇职工养老保险缴费记录及待遇查询', 'SOCIAL', '1.身份证\n2.社保卡', '1.携带证件到社保中心查询\n2.或登录社保网站在线查询', '社保服务热线：12333', 276, 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码',
  `permission_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `permission_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限类型：MENU / BUTTON / DATA_SCOPE',
  `module_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模块标识',
  `parent_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '父级权限编码',
  `route_path` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '前端路由路径，仅菜单权限使用',
  `sort_no` int NOT NULL DEFAULT 0 COMMENT '排序值',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_permission_code`(`permission_code` ASC) USING BTREE,
  INDEX `idx_permission_type_status`(`permission_type` ASC, `status` ASC) USING BTREE,
  INDEX `idx_permission_module`(`module_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 92 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1, 'menu.home', '首页', 'MENU', 'home', NULL, '/home', 10, 1, '首页菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (2, 'menu.activity', '社区活动', 'MENU', 'activity', NULL, '/activity', 20, 1, '社区活动菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (3, 'menu.volunteer', '志愿者管理', 'MENU', 'volunteer', NULL, '/volunteer', 30, 1, '志愿者菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (4, 'menu.guide', '办事指南', 'MENU', 'guide', NULL, '/guide', 40, 1, '办事指南菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (5, 'menu.appointment', '服务预约', 'MENU', 'appointment', NULL, '/appointment', 50, 1, '服务预约菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (6, 'menu.neighborhelp', '邻里互助', 'MENU', 'neighborhelp', NULL, '/neighborhelp', 60, 1, '邻里互助菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (7, 'menu.points', '积分中心', 'MENU', 'points', NULL, '/points', 70, 1, '积分中心菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (8, 'menu.message', '消息中心', 'MENU', 'message', NULL, '/message', 80, 1, '消息中心菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (9, 'menu.evaluation', '服务评价', 'MENU', 'evaluation', NULL, '/evaluation', 90, 1, '服务评价菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (10, 'menu.workorder', '工单管理', 'MENU', 'workorder', NULL, '/workorder', 100, 1, '工单管理菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (11, 'menu.workorder.overtime', '工单超时看板', 'MENU', 'workorder', 'menu.workorder', '/workorder-overtime', 110, 1, '工单超时看板菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (12, 'menu.hazard', '治安隐患', 'MENU', 'hazard', NULL, '/hazard', 120, 1, '治安隐患菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (13, 'menu.notice', '通知公告', 'MENU', 'notice', NULL, '/notice', 130, 1, '通知公告菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (14, 'menu.resident', '居民管理', 'MENU', 'resident', NULL, '/resident', 140, 1, '居民管理菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (15, 'menu.floating', '流动人口', 'MENU', 'floating', NULL, '/floating', 150, 1, '流动人口菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (16, 'menu.dispatch.rule', '派单规则', 'MENU', 'dispatch_rule', NULL, '/dispatch-rule', 160, 1, '派单规则菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (17, 'menu.user', '用户管理', 'MENU', 'user', NULL, '/user', 170, 1, '用户管理菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (18, 'menu.log', '操作日志', 'MENU', 'log', NULL, '/log', 180, 1, '操作日志菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (19, 'menu.profile', '个人中心', 'MENU', 'profile', NULL, '/profile', 190, 1, '个人中心菜单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (20, 'btn.workorder.add', '提交工单', 'BUTTON', 'workorder', 'menu.workorder', NULL, 1001, 1, '工单新增', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (21, 'btn.workorder.edit', '编辑工单', 'BUTTON', 'workorder', 'menu.workorder', NULL, 1002, 1, '工单编辑', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (22, 'btn.workorder.delete', '删除工单', 'BUTTON', 'workorder', 'menu.workorder', NULL, 1003, 1, '工单删除', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (23, 'btn.workorder.handle', '处理工单', 'BUTTON', 'workorder', 'menu.workorder', NULL, 1004, 1, '工单处理', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (24, 'btn.workorder.export', '导出工单', 'BUTTON', 'workorder', 'menu.workorder', NULL, 1005, 1, '工单导出', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (25, 'btn.volunteer.apply', '申请志愿者', 'BUTTON', 'volunteer', 'menu.volunteer', NULL, 1101, 1, '志愿者申请', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (26, 'btn.volunteer.audit', '审核志愿者', 'BUTTON', 'volunteer', 'menu.volunteer', NULL, 1102, 1, '志愿者审核', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (27, 'btn.volunteer.edit', '编辑志愿者', 'BUTTON', 'volunteer', 'menu.volunteer', NULL, 1103, 1, '志愿者编辑', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (28, 'btn.volunteer.delete', '删除志愿者', 'BUTTON', 'volunteer', 'menu.volunteer', NULL, 1104, 1, '志愿者删除', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (29, 'btn.user.add', '新增用户', 'BUTTON', 'user', 'menu.user', NULL, 1201, 1, '用户新增', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (30, 'btn.user.edit', '编辑用户', 'BUTTON', 'user', 'menu.user', NULL, 1202, 1, '用户编辑', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (31, 'btn.user.delete', '删除用户', 'BUTTON', 'user', 'menu.user', NULL, 1203, 1, '用户删除', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (32, 'btn.dispatch.rule.add', '新增派单规则', 'BUTTON', 'dispatch_rule', 'menu.dispatch.rule', NULL, 1301, 1, '派单规则新增', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (33, 'btn.dispatch.rule.edit', '编辑派单规则', 'BUTTON', 'dispatch_rule', 'menu.dispatch.rule', NULL, 1302, 1, '派单规则编辑', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (34, 'btn.dispatch.rule.delete', '删除派单规则', 'BUTTON', 'dispatch_rule', 'menu.dispatch.rule', NULL, 1303, 1, '派单规则删除', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (35, 'btn.dispatch.rule.preview', '预览派单规则', 'BUTTON', 'dispatch_rule', 'menu.dispatch.rule', NULL, 1304, 1, '派单规则预览', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (36, 'btn.notice.add', '发布通知', 'BUTTON', 'notice', 'menu.notice', NULL, 1401, 1, '通知新增', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (37, 'btn.notice.edit', '编辑通知', 'BUTTON', 'notice', 'menu.notice', NULL, 1402, 1, '通知编辑', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (38, 'btn.notice.delete', '删除通知', 'BUTTON', 'notice', 'menu.notice', NULL, 1403, 1, '通知删除', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (39, 'btn.notice.stats', '查看通知统计', 'BUTTON', 'notice', 'menu.notice', NULL, 1404, 1, '通知统计', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (40, 'btn.resident.add', '新增居民', 'BUTTON', 'resident', 'menu.resident', NULL, 1501, 1, '居民新增', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (41, 'btn.resident.edit', '编辑居民', 'BUTTON', 'resident', 'menu.resident', NULL, 1502, 1, '居民编辑', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (42, 'btn.resident.delete', '删除居民', 'BUTTON', 'resident', 'menu.resident', NULL, 1503, 1, '居民删除', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (43, 'btn.resident.export', '导出居民', 'BUTTON', 'resident', 'menu.resident', NULL, 1504, 1, '居民导出', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (44, 'btn.resident.family.add', '新增家庭关系', 'BUTTON', 'resident', 'menu.resident', NULL, 1505, 1, '家庭关系新增', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (45, 'btn.resident.family.delete', '删除家庭关系', 'BUTTON', 'resident', 'menu.resident', NULL, 1506, 1, '家庭关系删除', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (46, 'btn.activity.add', '发布活动', 'BUTTON', 'activity', 'menu.activity', NULL, 1601, 1, '活动新增', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (47, 'btn.activity.edit', '编辑活动', 'BUTTON', 'activity', 'menu.activity', NULL, 1602, 1, '活动编辑', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (48, 'btn.activity.delete', '删除活动', 'BUTTON', 'activity', 'menu.activity', NULL, 1603, 1, '活动删除', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (49, 'btn.guide.add', '发布指南', 'BUTTON', 'guide', 'menu.guide', NULL, 1701, 1, '指南新增', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (50, 'btn.guide.edit', '编辑指南', 'BUTTON', 'guide', 'menu.guide', NULL, 1702, 1, '指南编辑', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (51, 'btn.guide.delete', '删除指南', 'BUTTON', 'guide', 'menu.guide', NULL, 1703, 1, '指南删除', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (52, 'btn.appointment.add', '新增预约', 'BUTTON', 'appointment', 'menu.appointment', NULL, 1801, 1, '预约新增', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (53, 'btn.appointment.edit', '编辑预约', 'BUTTON', 'appointment', 'menu.appointment', NULL, 1802, 1, '预约编辑', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (54, 'btn.appointment.delete', '删除预约', 'BUTTON', 'appointment', 'menu.appointment', NULL, 1803, 1, '预约删除', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (55, 'btn.appointment.status.confirm', '确认预约', 'BUTTON', 'appointment', 'menu.appointment', NULL, 1804, 1, '预约确认', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (56, 'btn.appointment.status.complete', '完成预约', 'BUTTON', 'appointment', 'menu.appointment', NULL, 1805, 1, '预约完成', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (57, 'btn.appointment.status.cancel', '取消预约', 'BUTTON', 'appointment', 'menu.appointment', NULL, 1806, 1, '预约取消', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (58, 'btn.neighborhelp.add', '发布互助', 'BUTTON', 'neighborhelp', 'menu.neighborhelp', NULL, 1901, 1, '互助新增', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (59, 'btn.neighborhelp.edit', '编辑互助', 'BUTTON', 'neighborhelp', 'menu.neighborhelp', NULL, 1902, 1, '互助编辑', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (60, 'btn.neighborhelp.delete', '删除互助', 'BUTTON', 'neighborhelp', 'menu.neighborhelp', NULL, 1903, 1, '互助删除', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (61, 'btn.neighborhelp.status.complete', '完成互助', 'BUTTON', 'neighborhelp', 'menu.neighborhelp', NULL, 1904, 1, '互助完成', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (62, 'btn.neighborhelp.status.close', '关闭互助', 'BUTTON', 'neighborhelp', 'menu.neighborhelp', NULL, 1905, 1, '互助关闭', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (63, 'btn.message.read', '标记消息已读', 'BUTTON', 'message', 'menu.message', NULL, 2001, 1, '消息已读', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (64, 'btn.message.read_all', '全部消息已读', 'BUTTON', 'message', 'menu.message', NULL, 2002, 1, '消息全部已读', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (65, 'btn.message.admin_list', '查看消息管理列表', 'BUTTON', 'message', 'menu.message', NULL, 2003, 1, '消息管理列表', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (66, 'btn.evaluation.submit', '提交评价', 'BUTTON', 'evaluation', 'menu.evaluation', NULL, 2101, 1, '评价提交', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (67, 'btn.evaluation.admin_list', '查看评价管理列表', 'BUTTON', 'evaluation', 'menu.evaluation', NULL, 2102, 1, '评价管理列表', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (68, 'btn.evaluation.stats', '查看评价统计', 'BUTTON', 'evaluation', 'menu.evaluation', NULL, 2103, 1, '评价统计', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (69, 'btn.hazard.add', '上报隐患', 'BUTTON', 'hazard', 'menu.hazard', NULL, 2201, 1, '隐患新增', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (70, 'btn.hazard.edit', '编辑隐患', 'BUTTON', 'hazard', 'menu.hazard', NULL, 2202, 1, '隐患编辑', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (71, 'btn.hazard.delete', '删除隐患', 'BUTTON', 'hazard', 'menu.hazard', NULL, 2203, 1, '隐患删除', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (72, 'btn.hazard.handle', '处理隐患', 'BUTTON', 'hazard', 'menu.hazard', NULL, 2204, 1, '隐患处理', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (73, 'btn.floating.add', '登记流动人口', 'BUTTON', 'floating', 'menu.floating', NULL, 2301, 1, '流动人口新增', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (74, 'btn.floating.edit', '编辑流动人口', 'BUTTON', 'floating', 'menu.floating', NULL, 2302, 1, '流动人口编辑', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (75, 'btn.floating.delete', '删除流动人口', 'BUTTON', 'floating', 'menu.floating', NULL, 2303, 1, '流动人口删除', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (76, 'btn.log.delete', '删除日志', 'BUTTON', 'log', 'menu.log', NULL, 2401, 1, '日志删除', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (77, 'btn.log.clear', '清空日志', 'BUTTON', 'log', 'menu.log', NULL, 2402, 1, '日志清空', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (78, 'btn.profile.update', '修改个人资料', 'BUTTON', 'profile', 'menu.profile', NULL, 2501, 1, '个人资料修改', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (79, 'btn.profile.password', '修改个人密码', 'BUTTON', 'profile', 'menu.profile', NULL, 2502, 1, '个人密码修改', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (80, 'scope.workorder.self', '工单仅本人', 'DATA_SCOPE', 'workorder', 'menu.workorder', NULL, 3001, 1, '仅查看本人提交的工单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (81, 'scope.workorder.all', '工单全量', 'DATA_SCOPE', 'workorder', 'menu.workorder', NULL, 3002, 1, '查看全部工单', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (82, 'scope.appointment.self', '预约仅本人', 'DATA_SCOPE', 'appointment', 'menu.appointment', NULL, 3101, 1, '仅查看本人预约', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (83, 'scope.appointment.all', '预约全量', 'DATA_SCOPE', 'appointment', 'menu.appointment', NULL, 3102, 1, '查看全部预约', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (84, 'scope.message.self', '消息仅本人', 'DATA_SCOPE', 'message', 'menu.message', NULL, 3201, 1, '仅查看本人消息', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (85, 'scope.message.all', '消息全量', 'DATA_SCOPE', 'message', 'menu.message', NULL, 3202, 1, '查看全部消息', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (86, 'scope.evaluation.self', '评价仅本人', 'DATA_SCOPE', 'evaluation', 'menu.evaluation', NULL, 3301, 1, '仅查看本人评价', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (87, 'scope.evaluation.all', '评价全量', 'DATA_SCOPE', 'evaluation', 'menu.evaluation', NULL, 3302, 1, '查看全部评价', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (88, 'scope.volunteer.public_self', '志愿者公开加本人', 'DATA_SCOPE', 'volunteer', 'menu.volunteer', NULL, 3401, 1, '查看已通过志愿者与本人申请', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (89, 'scope.volunteer.all', '志愿者全量', 'DATA_SCOPE', 'volunteer', 'menu.volunteer', NULL, 3402, 1, '查看全部志愿者记录', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (90, 'scope.notice.published', '公告已发布可见', 'DATA_SCOPE', 'notice', 'menu.notice', NULL, 3501, 1, '仅查看已发布公告', '2026-03-12 22:20:18', '2026-03-12 22:20:18');
INSERT INTO `sys_permission` VALUES (91, 'scope.notice.all', '公告全量', 'DATA_SCOPE', 'notice', 'menu.notice', NULL, 3502, 1, '查看全部公告', '2026-03-12 22:20:18', '2026-03-12 22:20:18');

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码：ADMIN / USER',
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_permission`(`role_code` ASC, `permission_code` ASC) USING BTREE,
  INDEX `idx_role_code`(`role_code` ASC) USING BTREE,
  INDEX `idx_role_permission_code`(`permission_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 167 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (1, 'ADMIN', 'menu.home', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (2, 'ADMIN', 'menu.activity', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (3, 'ADMIN', 'menu.volunteer', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (4, 'ADMIN', 'menu.guide', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (5, 'ADMIN', 'menu.appointment', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (6, 'ADMIN', 'menu.neighborhelp', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (7, 'ADMIN', 'menu.points', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (8, 'ADMIN', 'menu.message', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (9, 'ADMIN', 'menu.evaluation', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (10, 'ADMIN', 'menu.workorder', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (11, 'ADMIN', 'menu.workorder.overtime', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (12, 'ADMIN', 'menu.hazard', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (13, 'ADMIN', 'menu.notice', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (14, 'ADMIN', 'menu.resident', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (15, 'ADMIN', 'menu.floating', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (16, 'ADMIN', 'menu.dispatch.rule', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (17, 'ADMIN', 'menu.user', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (18, 'ADMIN', 'menu.log', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (19, 'ADMIN', 'menu.profile', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (20, 'ADMIN', 'btn.workorder.add', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (21, 'ADMIN', 'btn.workorder.edit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (22, 'ADMIN', 'btn.workorder.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (23, 'ADMIN', 'btn.workorder.handle', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (24, 'ADMIN', 'btn.workorder.export', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (25, 'ADMIN', 'btn.volunteer.apply', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (26, 'ADMIN', 'btn.volunteer.audit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (27, 'ADMIN', 'btn.volunteer.edit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (28, 'ADMIN', 'btn.volunteer.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (29, 'ADMIN', 'btn.user.add', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (30, 'ADMIN', 'btn.user.edit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (31, 'ADMIN', 'btn.user.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (32, 'ADMIN', 'btn.dispatch.rule.add', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (33, 'ADMIN', 'btn.dispatch.rule.edit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (34, 'ADMIN', 'btn.dispatch.rule.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (35, 'ADMIN', 'btn.dispatch.rule.preview', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (36, 'ADMIN', 'btn.notice.add', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (37, 'ADMIN', 'btn.notice.edit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (38, 'ADMIN', 'btn.notice.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (39, 'ADMIN', 'btn.notice.stats', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (40, 'ADMIN', 'btn.resident.add', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (41, 'ADMIN', 'btn.resident.edit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (42, 'ADMIN', 'btn.resident.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (43, 'ADMIN', 'btn.resident.export', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (44, 'ADMIN', 'btn.resident.family.add', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (45, 'ADMIN', 'btn.resident.family.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (46, 'ADMIN', 'btn.activity.add', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (47, 'ADMIN', 'btn.activity.edit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (48, 'ADMIN', 'btn.activity.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (49, 'ADMIN', 'btn.guide.add', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (50, 'ADMIN', 'btn.guide.edit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (51, 'ADMIN', 'btn.guide.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (52, 'ADMIN', 'btn.appointment.add', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (53, 'ADMIN', 'btn.appointment.edit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (54, 'ADMIN', 'btn.appointment.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (55, 'ADMIN', 'btn.appointment.status.confirm', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (56, 'ADMIN', 'btn.appointment.status.complete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (57, 'ADMIN', 'btn.appointment.status.cancel', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (58, 'ADMIN', 'btn.neighborhelp.add', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (59, 'ADMIN', 'btn.neighborhelp.edit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (60, 'ADMIN', 'btn.neighborhelp.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (61, 'ADMIN', 'btn.neighborhelp.status.complete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (62, 'ADMIN', 'btn.neighborhelp.status.close', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (63, 'ADMIN', 'btn.message.read', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (64, 'ADMIN', 'btn.message.read_all', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (65, 'ADMIN', 'btn.message.admin_list', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (66, 'ADMIN', 'btn.evaluation.submit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (67, 'ADMIN', 'btn.evaluation.admin_list', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (68, 'ADMIN', 'btn.evaluation.stats', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (69, 'ADMIN', 'btn.hazard.add', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (70, 'ADMIN', 'btn.hazard.edit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (71, 'ADMIN', 'btn.hazard.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (72, 'ADMIN', 'btn.hazard.handle', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (73, 'ADMIN', 'btn.floating.add', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (74, 'ADMIN', 'btn.floating.edit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (75, 'ADMIN', 'btn.floating.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (76, 'ADMIN', 'btn.log.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (77, 'ADMIN', 'btn.log.clear', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (78, 'ADMIN', 'btn.profile.update', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (79, 'ADMIN', 'btn.profile.password', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (80, 'ADMIN', 'scope.workorder.self', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (81, 'ADMIN', 'scope.workorder.all', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (82, 'ADMIN', 'scope.appointment.self', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (83, 'ADMIN', 'scope.appointment.all', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (84, 'ADMIN', 'scope.message.self', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (85, 'ADMIN', 'scope.message.all', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (86, 'ADMIN', 'scope.evaluation.self', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (87, 'ADMIN', 'scope.evaluation.all', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (88, 'ADMIN', 'scope.volunteer.public_self', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (89, 'ADMIN', 'scope.volunteer.all', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (90, 'ADMIN', 'scope.notice.published', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (91, 'ADMIN', 'scope.notice.all', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (128, 'USER', 'menu.home', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (129, 'USER', 'menu.activity', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (130, 'USER', 'menu.volunteer', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (131, 'USER', 'menu.guide', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (132, 'USER', 'menu.appointment', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (133, 'USER', 'menu.neighborhelp', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (134, 'USER', 'menu.points', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (135, 'USER', 'menu.message', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (136, 'USER', 'menu.evaluation', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (137, 'USER', 'menu.workorder', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (138, 'USER', 'menu.hazard', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (139, 'USER', 'menu.notice', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (140, 'USER', 'menu.profile', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (141, 'USER', 'btn.workorder.add', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (142, 'USER', 'btn.workorder.edit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (143, 'USER', 'btn.workorder.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (144, 'USER', 'btn.volunteer.apply', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (145, 'USER', 'btn.volunteer.edit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (146, 'USER', 'btn.volunteer.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (147, 'USER', 'btn.appointment.add', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (148, 'USER', 'btn.appointment.edit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (149, 'USER', 'btn.appointment.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (150, 'USER', 'btn.neighborhelp.add', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (151, 'USER', 'btn.neighborhelp.edit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (152, 'USER', 'btn.neighborhelp.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (153, 'USER', 'btn.message.read', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (154, 'USER', 'btn.message.read_all', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (155, 'USER', 'btn.evaluation.submit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (156, 'USER', 'btn.hazard.add', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (157, 'USER', 'btn.hazard.edit', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (158, 'USER', 'btn.hazard.delete', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (159, 'USER', 'btn.profile.update', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (160, 'USER', 'btn.profile.password', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (161, 'USER', 'scope.workorder.self', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (162, 'USER', 'scope.appointment.self', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (163, 'USER', 'scope.message.self', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (164, 'USER', 'scope.evaluation.self', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (165, 'USER', 'scope.volunteer.public_self', '2026-03-12 22:20:18');
INSERT INTO `sys_role_permission` VALUES (166, 'USER', 'scope.notice.published', '2026-03-12 22:20:18');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（明文）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'https://tu.ltyuanfang.cn/api/fengjing.php' COMMENT '头像',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '13800000001', 'admin@community.com', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'ADMIN', 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `sys_user` VALUES (2, 'zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13800000002', 'zhangsan@qq.com', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'USER', 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `sys_user` VALUES (3, 'lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', '13800000003', 'lisi@qq.com', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'USER', 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `sys_user` VALUES (4, 'wangwu', 'e10adc3949ba59abbe56e057f20f883e', '王五', '13800000004', 'wangwu@163.com', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'USER', 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `sys_user` VALUES (5, 'zhaoliu', 'e10adc3949ba59abbe56e057f20f883e', '赵六', '13800000005', 'zhaoliu@163.com', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'USER', 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `sys_user` VALUES (6, 'sunqi', 'e10adc3949ba59abbe56e057f20f883e', '孙七', '13800000006', 'sunqi@gmail.com', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'USER', 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `sys_user` VALUES (7, 'zhouba', 'e10adc3949ba59abbe56e057f20f883e', '周八', '13800000007', 'zhouba@gmail.com', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'ADMIN', 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `sys_user` VALUES (8, 'wujiu', 'e10adc3949ba59abbe56e057f20f883e', '吴九', '13800000008', 'wujiu@qq.com', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'USER', 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `sys_user` VALUES (9, 'zhengshi', 'e10adc3949ba59abbe56e057f20f883e', '郑十', '13800000009', 'zhengshi@qq.com', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'USER', 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `sys_user` VALUES (10, 'qianyi', 'e10adc3949ba59abbe56e057f20f883e', '钱一', '13800000010', 'qianyi@163.com', 'https://tu.ltyuanfang.cn/api/fengjing.php', 'USER', 1, '2026-03-12 22:19:30', '2026-03-12 22:19:30');

-- ----------------------------
-- Table structure for volunteer
-- ----------------------------
DROP TABLE IF EXISTS `volunteer`;
CREATE TABLE `volunteer`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `skills` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '技能特长',
  `service_hours` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '服务时长',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-待审核，1-已通过，2-已拒绝',
  `join_date` date NULL DEFAULT NULL COMMENT '加入日期',
  `photo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'https://tu.ltyuanfang.cn/api/fengjing.php' COMMENT '照片',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '志愿者表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of volunteer
-- ----------------------------
INSERT INTO `volunteer` VALUES (1, 2, '张三', '13800000002', '电脑维修、文案写作', 50.50, 1, '2023-05-01', 'https://tu.ltyuanfang.cn/api/fengjing.php', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `volunteer` VALUES (2, 3, '李四', '13800000003', '医疗护理、心理咨询', 120.00, 1, '2022-03-15', 'https://tu.ltyuanfang.cn/api/fengjing.php', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `volunteer` VALUES (3, 4, '王五', '13800000004', '法律咨询、纠纷调解', 30.00, 1, '2024-01-10', 'https://tu.ltyuanfang.cn/api/fengjing.php', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `volunteer` VALUES (4, 5, '赵六', '13800000005', '摄影摄像、视频剪辑', 15.50, 0, NULL, 'https://tu.ltyuanfang.cn/api/fengjing.php', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `volunteer` VALUES (5, 6, '孙七', '13800000006', '手工制作、绘画教学', 60.00, 1, '2023-08-20', 'https://tu.ltyuanfang.cn/api/fengjing.php', '2026-03-12 22:19:30', '2026-03-12 22:19:30');
INSERT INTO `volunteer` VALUES (6, 8, '吴九', '13800000008', '水电维修、家电维修', 80.00, 1, '2022-11-01', 'https://tu.ltyuanfang.cn/api/fengjing.php', '2026-03-12 22:19:30', '2026-03-12 22:19:30');

-- ----------------------------
-- Table structure for work_order
-- ----------------------------
DROP TABLE IF EXISTS `work_order`;
CREATE TABLE `work_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工单标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '工单内容',
  `order_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工单类型：REPAIR-报修，COMPLAINT-投诉，SUGGESTION-建议，OTHER-其他',
  `images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片，多个用逗号分隔',
  `submitter_id` bigint NULL DEFAULT NULL COMMENT '提交人ID',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `assignee_id` bigint NULL DEFAULT NULL COMMENT '责任人ID',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-待处理，1-处理中，2-已完成，3-已关闭',
  `priority` tinyint NULL DEFAULT 1 COMMENT '优先级：0-低，1-中，2-高',
  `deadline` datetime NULL DEFAULT NULL COMMENT '要求完成时间',
  `is_overtime` tinyint NOT NULL DEFAULT 0 COMMENT '是否超时：0-否，1-是',
  `handle_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '处理结果',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_work_order_deadline`(`deadline` ASC) USING BTREE,
  INDEX `idx_work_order_assignee`(`assignee_id` ASC) USING BTREE,
  INDEX `idx_work_order_overtime_status`(`is_overtime` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '工单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of work_order
-- ----------------------------
INSERT INTO `work_order` VALUES (1, '楼道灯不亮', '1号楼2单元3楼楼道灯损坏，晚上很黑不安全', 'REPAIR', 'https://tu.ltyuanfang.cn/api/fengjing.php', 2, 1, NULL, 2, 1, NULL, 0, '已更换灯泡，恢复正常', '2026-01-08 15:00:00', '2026-01-05 10:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (2, '邻居噪音扰民', '楼上住户经常深夜制造噪音，严重影响休息', 'COMPLAINT', NULL, 3, 1, NULL, 1, 2, NULL, 0, NULL, NULL, '2026-01-06 14:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (3, '建议增设健身器材', '建议在小区广场增设一些健身器材', 'SUGGESTION', NULL, 4, NULL, NULL, 0, 0, NULL, 0, NULL, NULL, '2026-01-07 09:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (4, '电梯故障', '2号楼电梯经常出现异响，存在安全隐患', 'REPAIR', 'https://tu.ltyuanfang.cn/api/fengjing.php', 5, 7, NULL, 1, 2, NULL, 0, NULL, NULL, '2026-01-08 11:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (5, '垃圾桶满溢', '3号楼门口垃圾桶已满，无人清理', 'COMPLAINT', 'https://tu.ltyuanfang.cn/api/fengjing.php', 6, 1, NULL, 2, 1, NULL, 0, '已通知保洁清理完毕', '2026-01-09 10:00:00', '2026-01-09 08:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (6, '路灯损坏', '小区东门附近路灯损坏，夜间出行不便', 'REPAIR', NULL, 2, NULL, NULL, 0, 1, NULL, 0, NULL, NULL, '2026-01-10 16:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (7, '下水道堵塞', '4号楼1单元下水道堵塞，污水外溢', 'REPAIR', 'https://tu.ltyuanfang.cn/api/fengjing.php', 3, 7, NULL, 2, 2, NULL, 0, '已疏通下水道', '2026-01-10 11:00:00', '2026-01-10 09:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (8, '门禁卡消磁', '门禁卡无法正常使用，需要重新办理', 'OTHER', NULL, 5, 1, NULL, 2, 0, NULL, 0, '已补办新卡', '2026-01-06 14:00:00', '2026-01-06 10:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (9, '暖气不热', '家里暖气温度不够，室温只有15度', 'REPAIR', NULL, 2, 1, NULL, 2, 2, NULL, 0, '已调整供暖阀门', '2025-12-20 14:00:00', '2025-12-18 09:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (10, '停车位被占', '我的固定车位经常被外来车辆占用', 'COMPLAINT', NULL, 3, 1, NULL, 2, 1, NULL, 0, '已加强巡逻管理', '2025-12-22 10:00:00', '2025-12-20 15:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (11, '建议增加快递柜', '小区快递柜数量不足，建议增加', 'SUGGESTION', NULL, 4, NULL, NULL, 2, 0, NULL, 0, '已采纳，计划下月安装', '2025-12-25 11:00:00', '2025-12-15 14:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (12, '窗户漏风', '卧室窗户密封不好，冬天漏风', 'REPAIR', NULL, 5, 7, NULL, 2, 1, NULL, 0, '已更换密封条', '2025-12-28 16:00:00', '2025-12-25 10:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (13, '物业费疑问', '对本月物业费账单有疑问', 'OTHER', NULL, 6, 1, NULL, 2, 0, NULL, 0, '已解释清楚', '2025-12-30 09:00:00', '2025-12-28 11:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (14, '年底安全检查', '年底消防安全检查发现问题', 'REPAIR', NULL, 2, 7, NULL, 2, 2, NULL, 0, '已整改', '2025-12-30 16:00:00', '2025-12-29 10:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (15, '楼顶漏水', '顶楼住户反映下雨天楼顶漏水', 'REPAIR', 'https://tu.ltyuanfang.cn/api/fengjing.php', 2, 7, NULL, 2, 2, NULL, 0, '已修补防水层', '2025-11-20 15:00:00', '2025-11-15 08:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (16, '广场舞噪音', '晚上广场舞音乐声音太大', 'COMPLAINT', NULL, 3, 1, NULL, 2, 1, NULL, 0, '已协调降低音量', '2025-11-18 10:00:00', '2025-11-16 19:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (17, '建议延长物业服务时间', '希望物业服务时间能延长到晚上8点', 'SUGGESTION', NULL, 4, NULL, NULL, 2, 0, NULL, 0, '已采纳调整', '2025-11-25 14:00:00', '2025-11-20 09:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (18, '门锁故障', '单元门电子锁经常打不开', 'REPAIR', NULL, 5, 1, NULL, 2, 2, NULL, 0, '已更换门锁', '2025-11-28 11:00:00', '2025-11-25 16:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (19, '供暖问题', '暖气片不热需要检修', 'REPAIR', NULL, 6, 7, NULL, 2, 1, NULL, 0, '已维修', '2025-11-30 10:00:00', '2025-11-28 09:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (20, '绿化带杂草', '小区绿化带杂草丛生需要清理', 'COMPLAINT', NULL, 2, 7, NULL, 2, 0, NULL, 0, '已安排清理', '2025-10-20 10:00:00', '2025-10-18 14:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (21, '电表故障', '电表显示异常，用电量不准确', 'REPAIR', NULL, 3, 1, NULL, 2, 1, NULL, 0, '已更换电表', '2025-10-25 15:00:00', '2025-10-22 09:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (22, '建议增设监控', '建议在停车场增设监控摄像头', 'SUGGESTION', NULL, 4, NULL, NULL, 2, 1, NULL, 0, '已列入计划', '2025-10-28 11:00:00', '2025-10-25 10:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (23, '国庆期间噪音', '国庆期间装修噪音扰民', 'COMPLAINT', NULL, 5, 1, NULL, 2, 1, NULL, 0, '已协调', '2025-10-08 14:00:00', '2025-10-05 10:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (24, '空调外机噪音', '邻居空调外机噪音太大', 'COMPLAINT', NULL, 5, 1, NULL, 2, 1, NULL, 0, '已协调处理', '2025-09-20 14:00:00', '2025-09-18 20:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (25, '墙面脱落', '楼道墙面涂料脱落', 'REPAIR', 'https://tu.ltyuanfang.cn/api/fengjing.php', 6, 7, NULL, 2, 0, NULL, 0, '已重新粉刷', '2025-09-25 16:00:00', '2025-09-22 11:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (26, '建议增加休息座椅', '小区步道建议增加休息座椅', 'SUGGESTION', NULL, 2, NULL, NULL, 2, 0, NULL, 0, '已采纳', '2025-09-28 10:00:00', '2025-09-25 15:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (27, '水管爆裂', '地下室水管爆裂漏水', 'REPAIR', 'https://tu.ltyuanfang.cn/api/fengjing.php', 3, 1, NULL, 2, 2, NULL, 0, '已紧急抢修', '2025-08-15 18:00:00', '2025-08-15 10:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (28, '乱扔垃圾', '有人从楼上乱扔垃圾', 'COMPLAINT', NULL, 4, 7, NULL, 2, 2, NULL, 0, '已加强监管', '2025-08-20 11:00:00', '2025-08-18 09:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (29, '高温天气用电问题', '夏季用电高峰期电压不稳', 'REPAIR', NULL, 5, 1, NULL, 2, 2, NULL, 0, '已联系供电局', '2025-08-25 15:00:00', '2025-08-22 10:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (30, '电梯按钮失灵', '3号楼电梯部分按钮失灵', 'REPAIR', NULL, 5, 1, NULL, 2, 1, NULL, 0, '已维修', '2025-07-20 14:00:00', '2025-07-18 08:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (31, '遛狗不牵绳', '有居民遛狗不牵绳，存在安全隐患', 'COMPLAINT', NULL, 6, 1, NULL, 2, 1, NULL, 0, '已加强宣传', '2025-07-25 10:00:00', '2025-07-22 17:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (32, '空调漏水', '空调外机漏水影响楼下', 'COMPLAINT', NULL, 2, 7, NULL, 2, 1, NULL, 0, '已处理', '2025-07-28 11:00:00', '2025-07-26 09:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (33, '建议增加遮阳棚', '停车场建议增加遮阳棚', 'SUGGESTION', NULL, 3, NULL, NULL, 2, 0, NULL, 0, '已采纳', '2025-07-30 10:00:00', '2025-07-28 14:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (34, '路面破损', '小区内部道路有坑洼', 'REPAIR', 'https://tu.ltyuanfang.cn/api/fengjing.php', 2, 7, NULL, 2, 1, NULL, 0, '已修补', '2025-06-20 15:00:00', '2025-06-18 11:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (35, '建议增加儿童游乐设施', '希望增加更多儿童游乐设施', 'SUGGESTION', NULL, 3, NULL, NULL, 2, 0, NULL, 0, '已列入计划', '2025-06-25 09:00:00', '2025-06-22 14:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (36, '蚊虫问题', '小区绿化带蚊虫较多', 'COMPLAINT', NULL, 4, 1, NULL, 2, 0, NULL, 0, '已安排消杀', '2025-06-28 10:00:00', '2025-06-25 16:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (37, '消防栓漏水', '1号楼消防栓漏水', 'REPAIR', NULL, 4, 1, NULL, 2, 2, NULL, 0, '已维修', '2025-05-18 11:00:00', '2025-05-15 09:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (38, '违规装修噪音', '邻居装修噪音超时', 'COMPLAINT', NULL, 5, 7, NULL, 2, 1, NULL, 0, '已协调', '2025-05-22 14:00:00', '2025-05-20 08:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (39, '五一期间安保', '五一期间安保问题反馈', 'OTHER', NULL, 6, 1, NULL, 2, 0, NULL, 0, '已加强', '2025-05-05 10:00:00', '2025-05-02 09:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (40, '草坪需要修剪', '小区草坪长得太高需要修剪', 'COMPLAINT', NULL, 6, 1, NULL, 2, 0, NULL, 0, '已安排修剪', '2025-04-20 10:00:00', '2025-04-18 15:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (41, '建议增加垃圾分类桶', '建议增加垃圾分类投放点', 'SUGGESTION', NULL, 2, NULL, NULL, 2, 0, NULL, 0, '已采纳', '2025-04-25 11:00:00', '2025-04-22 09:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (42, '春季绿化问题', '部分绿化带植物枯死', 'REPAIR', NULL, 3, 7, NULL, 2, 0, NULL, 0, '已补种', '2025-04-28 14:00:00', '2025-04-25 10:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (43, '楼道堆放杂物', '有居民在楼道堆放杂物', 'COMPLAINT', NULL, 3, 7, NULL, 2, 1, NULL, 0, '已清理', '2025-03-20 14:00:00', '2025-03-18 10:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (44, '门禁系统故障', '单元门禁系统故障', 'REPAIR', NULL, 4, 1, NULL, 2, 2, NULL, 0, '已修复', '2025-03-25 16:00:00', '2025-03-22 11:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (45, '建议增加充电桩', '电动车充电桩不够用', 'SUGGESTION', NULL, 5, NULL, NULL, 2, 1, NULL, 0, '已规划', '2025-03-28 10:00:00', '2025-03-25 09:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (46, '暖气管道漏水', '暖气管道接口处漏水', 'REPAIR', NULL, 5, 1, NULL, 2, 2, NULL, 0, '已维修', '2025-02-18 15:00:00', '2025-02-15 09:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (47, '停车乱象', '外来车辆乱停乱放', 'COMPLAINT', NULL, 6, 7, NULL, 2, 1, NULL, 0, '已加强管理', '2025-02-22 10:00:00', '2025-02-20 14:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (48, '春节期间值班问题', '春节期间物业值班人员不足', 'OTHER', NULL, 2, 1, NULL, 2, 1, NULL, 0, '已调整', '2025-02-10 11:00:00', '2025-02-08 10:00:00', '2026-03-12 22:19:30');
INSERT INTO `work_order` VALUES (49, '3 栋 5 楼照明故障报修', '3 栋 5 楼区域照明设施故障，影响居民夜间通行。存在老人跌倒安全隐患，需尽快处理。[请补充具体位置及联系人电话]', 'REPAIR', NULL, 1, NULL, 1, 0, 2, NULL, 0, NULL, NULL, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
