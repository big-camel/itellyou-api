/*
 Navicat Premium Data Transfer

 Source Server         : 47.110.147.62
 Source Server Type    : MySQL
 Source Server Version : 80020
 Source Host           : 47.110.147.62:9527
 Source Schema         : itellyou

 Target Server Type    : MySQL
 Target Server Version : 80020
 File Encoding         : 65001

 Date: 26/08/2020 21:13:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for WORKER_NODE
-- ----------------------------
DROP TABLE IF EXISTS `WORKER_NODE`;
CREATE TABLE `WORKER_NODE`  (
  `ID` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',
  `HOST_NAME` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'host name',
  `PORT` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'port',
  `TYPE` int(0) NOT NULL COMMENT 'node type: ACTUAL or CONTAINER',
  `LAUNCH_DATE` date NOT NULL COMMENT 'launch date',
  `MODIFIED` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'modified time',
  `CREATED` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'created time',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 101 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'DB WorkerID Assigner for UID Generator' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ali_config
-- ----------------------------
DROP TABLE IF EXISTS `ali_config`;
CREATE TABLE `ali_config`  (
  `id` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `secret` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ali_dm_config
-- ----------------------------
DROP TABLE IF EXISTS `ali_dm_config`;
CREATE TABLE `ali_dm_config`  (
  `type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `minute` int(0) NOT NULL DEFAULT 1,
  `hour` int(0) NOT NULL DEFAULT 5,
  `day` int(0) NOT NULL DEFAULT 10,
  PRIMARY KEY (`type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ali_dm_log
-- ----------------------------
DROP TABLE IF EXISTS `ali_dm_log`;
CREATE TABLE `ali_dm_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `email` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `template_id` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `data` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` tinyint(1) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 182 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ali_dm_template
-- ----------------------------
DROP TABLE IF EXISTS `ali_dm_template`;
CREATE TABLE `ali_dm_template`  (
  `id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `param` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `send_addr` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `send_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `title` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `tag_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `expire` int(0) NULL DEFAULT 3600,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ali_sms_config
-- ----------------------------
DROP TABLE IF EXISTS `ali_sms_config`;
CREATE TABLE `ali_sms_config`  (
  `type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `minute` int(0) NOT NULL DEFAULT 1,
  `hour` int(0) NOT NULL DEFAULT 5,
  `day` int(0) NOT NULL DEFAULT 10,
  PRIMARY KEY (`type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ali_sms_log
-- ----------------------------
DROP TABLE IF EXISTS `ali_sms_log`;
CREATE TABLE `ali_sms_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `template_id` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `data` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` tinyint(1) NULL DEFAULT 0,
  `created_time` bigint(0) NOT NULL DEFAULT 0,
  `created_ip` bigint(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `mobile`(`mobile`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 531 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ali_sms_template
-- ----------------------------
DROP TABLE IF EXISTS `ali_sms_template`;
CREATE TABLE `ali_sms_template`  (
  `id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `code` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `param` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `sign_name` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `expire` int(0) NOT NULL DEFAULT 3600,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for alipay_config
-- ----------------------------
DROP TABLE IF EXISTS `alipay_config`;
CREATE TABLE `alipay_config`  (
  `app_id` varchar(180) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `private_key` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `public_key` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `alipay_key` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `gateway` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `public_cert_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `alipay_cert_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `root_cert_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `notify_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `return_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `redirect_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `is_enable` tinyint(1) NULL DEFAULT 0,
  `is_default` tinyint(1) NULL DEFAULT 0,
  PRIMARY KEY (`app_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_comment
-- ----------------------------
DROP TABLE IF EXISTS `article_comment`;
CREATE TABLE `article_comment`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(0) NULL DEFAULT 0,
  `parent_id` bigint(0) NULL DEFAULT NULL,
  `reply_id` bigint(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `html` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `comment_count` int(0) NULL DEFAULT 0,
  `support` int(0) NULL DEFAULT 0,
  `oppose` int(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `article_id`(`article_id`) USING BTREE,
  INDEX `parent_id`(`parent_id`) USING BTREE,
  INDEX `reply_id`(`reply_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_comment_vote
-- ----------------------------
DROP TABLE IF EXISTS `article_comment_vote`;
CREATE TABLE `article_comment_vote`  (
  `type` int(0) NOT NULL DEFAULT 0,
  `comment_id` bigint(0) NOT NULL,
  `created_user_id` bigint(0) NOT NULL,
  `created_time` bigint(0) NOT NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`comment_id`, `created_user_id`) USING BTREE,
  INDEX `type`(`type`, `comment_id`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_info
-- ----------------------------
DROP TABLE IF EXISTS `article_info`;
CREATE TABLE `article_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `custom_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `column_id` bigint(0) NULL DEFAULT NULL,
  `source_type` int(0) NULL DEFAULT 1,
  `source_data` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `is_published` tinyint(1) NULL DEFAULT 0 COMMENT '1已发布 0未发布',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `is_disabled` tinyint(1) NULL DEFAULT 1 COMMENT '1正常，2禁用',
  `version` int(0) NULL DEFAULT 0 COMMENT '已发布版本',
  `draft` int(0) NULL DEFAULT 0,
  `comment_count` int(0) NULL DEFAULT 0,
  `view` int(0) NULL DEFAULT 0,
  `support` int(0) NULL DEFAULT 0,
  `oppose` int(0) NULL DEFAULT 0,
  `star_count` int(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `is_published`(`is_published`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 278 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_paid_read
-- ----------------------------
DROP TABLE IF EXISTS `article_paid_read`;
CREATE TABLE `article_paid_read`  (
  `article_id` bigint(0) NOT NULL DEFAULT 0,
  `paid_type` int(0) NULL DEFAULT 0,
  `paid_to_read` tinyint(1) NULL DEFAULT 0,
  `paid_amount` decimal(10, 2) NULL DEFAULT 0.00,
  `star_to_read` tinyint(1) NULL DEFAULT 0,
  `free_read_scale` decimal(10, 2) NULL DEFAULT 0.00,
  PRIMARY KEY (`article_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_star
-- ----------------------------
DROP TABLE IF EXISTS `article_star`;
CREATE TABLE `article_star`  (
  `article_id` bigint(0) NOT NULL,
  `created_time` bigint(0) NOT NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NOT NULL,
  PRIMARY KEY (`article_id`, `created_user_id`) USING BTREE,
  INDEX `article_id`(`article_id`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_tag
-- ----------------------------
DROP TABLE IF EXISTS `article_tag`;
CREATE TABLE `article_tag`  (
  `article_id` bigint(0) NOT NULL,
  `tag_id` bigint(0) NOT NULL,
  PRIMARY KEY (`article_id`, `tag_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_version
-- ----------------------------
DROP TABLE IF EXISTS `article_version`;
CREATE TABLE `article_version`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(0) NOT NULL,
  `column_id` bigint(0) NULL DEFAULT NULL,
  `source_type` int(0) NULL DEFAULT 1,
  `source_data` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `html` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `version` int(0) NULL DEFAULT 0,
  `is_reviewed` tinyint(1) NULL DEFAULT 0 COMMENT '0 未审核 1 已审核',
  `is_disabled` tinyint(1) NULL DEFAULT 0 COMMENT '0 未禁用，1 禁用',
  `is_published` tinyint(1) NULL DEFAULT 0 COMMENT '0.未发布 1.发布',
  `save_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `article_id_2`(`article_id`, `version`) USING BTREE,
  INDEX `column_id`(`column_id`) USING BTREE,
  INDEX `article_id`(`article_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1466 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_version_tag
-- ----------------------------
DROP TABLE IF EXISTS `article_version_tag`;
CREATE TABLE `article_version_tag`  (
  `version` bigint(0) NOT NULL,
  `tag` bigint(0) NOT NULL,
  PRIMARY KEY (`version`, `tag`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_vote
-- ----------------------------
DROP TABLE IF EXISTS `article_vote`;
CREATE TABLE `article_vote`  (
  `type` int(0) NOT NULL DEFAULT 0,
  `article_id` bigint(0) NOT NULL,
  `created_user_id` bigint(0) NOT NULL,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`article_id`, `created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for collab_config
-- ----------------------------
DROP TABLE IF EXISTS `collab_config`;
CREATE TABLE `collab_config`  (
  `key` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for collab_info
-- ----------------------------
DROP TABLE IF EXISTS `collab_info`;
CREATE TABLE `collab_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `key` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `token` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `host` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `is_disabled` tinyint(1) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT NULL,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `token`(`token`) USING BTREE,
  INDEX `key`(`key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 719 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for column_info
-- ----------------------------
DROP TABLE IF EXISTS `column_info`;
CREATE TABLE `column_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `description` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `is_disabled` tinyint(1) NULL DEFAULT 1 COMMENT '1正常，2禁用',
  `is_reviewed` tinyint(1) NULL DEFAULT 0 COMMENT '0 未审核 1 已审核',
  `member_count` int(0) NULL DEFAULT 0,
  `article_count` int(0) NULL DEFAULT 0,
  `star_count` int(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1008 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for column_member
-- ----------------------------
DROP TABLE IF EXISTS `column_member`;
CREATE TABLE `column_member`  (
  `column_id` bigint(0) NOT NULL,
  `user_id` bigint(0) NOT NULL,
  `created_time` bigint(0) NOT NULL DEFAULT 0,
  `created_ip` bigint(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`column_id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for column_star
-- ----------------------------
DROP TABLE IF EXISTS `column_star`;
CREATE TABLE `column_star`  (
  `column_id` bigint(0) NOT NULL,
  `created_time` bigint(0) NOT NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NOT NULL,
  PRIMARY KEY (`column_id`, `created_user_id`) USING BTREE,
  INDEX `column_id`(`column_id`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for column_tag
-- ----------------------------
DROP TABLE IF EXISTS `column_tag`;
CREATE TABLE `column_tag`  (
  `column_id` bigint(0) NOT NULL,
  `tag_id` bigint(0) NOT NULL,
  PRIMARY KEY (`column_id`, `tag_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for geetest_config
-- ----------------------------
DROP TABLE IF EXISTS `geetest_config`;
CREATE TABLE `geetest_config`  (
  `id` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for geetest_log
-- ----------------------------
DROP TABLE IF EXISTS `geetest_log`;
CREATE TABLE `geetest_log`  (
  `key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `client_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `client_ip` bigint(0) NOT NULL DEFAULT 0,
  `status` tinyint(1) NOT NULL DEFAULT 1,
  `mode` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'unknow',
  `created_user_id` bigint(0) NOT NULL DEFAULT 0,
  `created_time` bigint(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`key`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE,
  INDEX `mode`(`mode`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for github_config
-- ----------------------------
DROP TABLE IF EXISTS `github_config`;
CREATE TABLE `github_config`  (
  `id` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `gateway` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `redirect_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_answer
-- ----------------------------
DROP TABLE IF EXISTS `question_answer`;
CREATE TABLE `question_answer`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `question_id` bigint(0) NULL DEFAULT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `version` int(0) NULL DEFAULT 0,
  `draft` int(0) NULL DEFAULT NULL,
  `is_published` tinyint(1) NULL DEFAULT 0 COMMENT '1已发布 0未发布',
  `is_disabled` tinyint(1) NULL DEFAULT 1 COMMENT '1正常，2禁用',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `is_adopted` tinyint(1) NULL DEFAULT NULL,
  `comments` int(0) NULL DEFAULT 0,
  `support` int(0) NULL DEFAULT 0,
  `oppose` int(0) NULL DEFAULT 0,
  `view` int(0) NULL DEFAULT 0,
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `star_count` int(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `question_id`(`question_id`) USING BTREE,
  INDEX `is_published`(`is_published`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 75 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_answer_comment
-- ----------------------------
DROP TABLE IF EXISTS `question_answer_comment`;
CREATE TABLE `question_answer_comment`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `answer_id` bigint(0) NULL DEFAULT 0,
  `parent_id` bigint(0) NULL DEFAULT NULL,
  `reply_id` bigint(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `html` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `comments` int(0) NULL DEFAULT 0,
  `support` int(0) NULL DEFAULT 0,
  `oppose` int(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `parent_id`(`parent_id`) USING BTREE,
  INDEX `answer_id`(`answer_id`) USING BTREE,
  INDEX `is_deleted`(`is_deleted`) USING BTREE,
  INDEX `support`(`support`) USING BTREE,
  INDEX `oppose`(`oppose`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 99 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_answer_comment_vote
-- ----------------------------
DROP TABLE IF EXISTS `question_answer_comment_vote`;
CREATE TABLE `question_answer_comment_vote`  (
  `type` int(0) NOT NULL DEFAULT 0,
  `comment_id` bigint(0) NOT NULL,
  `created_user_id` bigint(0) NOT NULL,
  `created_time` bigint(0) NOT NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`comment_id`, `created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_answer_paid_read
-- ----------------------------
DROP TABLE IF EXISTS `question_answer_paid_read`;
CREATE TABLE `question_answer_paid_read`  (
  `answer_id` bigint(0) NOT NULL DEFAULT 0,
  `paid_type` int(0) NULL DEFAULT 0,
  `paid_to_read` tinyint(1) NULL DEFAULT 0,
  `paid_amount` decimal(10, 2) NULL DEFAULT 0.00,
  `star_to_read` tinyint(1) NULL DEFAULT 0,
  `free_read_scale` decimal(10, 2) NULL DEFAULT 0.00,
  PRIMARY KEY (`answer_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_answer_star
-- ----------------------------
DROP TABLE IF EXISTS `question_answer_star`;
CREATE TABLE `question_answer_star`  (
  `answer_id` bigint(0) NOT NULL,
  `created_time` bigint(0) NOT NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NOT NULL,
  PRIMARY KEY (`answer_id`, `created_user_id`) USING BTREE,
  INDEX `answer_id`(`answer_id`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_answer_version
-- ----------------------------
DROP TABLE IF EXISTS `question_answer_version`;
CREATE TABLE `question_answer_version`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `answer_id` bigint(0) NOT NULL DEFAULT 0,
  `is_reviewed` tinyint(1) NULL DEFAULT 0 COMMENT '0 未审核 1 已审核',
  `is_disabled` tinyint(1) NULL DEFAULT 0 COMMENT '0 未禁用，1 禁用',
  `is_published` tinyint(1) NULL DEFAULT 0 COMMENT '0.未发布 1.发布',
  `content` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `html` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `version` int(0) NULL DEFAULT 0,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
  `save_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT NULL,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `comment_id`(`answer_id`, `version`) USING BTREE,
  INDEX `answer_id`(`answer_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 228 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_answer_vote
-- ----------------------------
DROP TABLE IF EXISTS `question_answer_vote`;
CREATE TABLE `question_answer_vote`  (
  `type` int(0) NOT NULL DEFAULT 0,
  `answer_id` bigint(0) NOT NULL,
  `created_user_id` bigint(0) NOT NULL,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`answer_id`, `created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_comment
-- ----------------------------
DROP TABLE IF EXISTS `question_comment`;
CREATE TABLE `question_comment`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `question_id` bigint(0) NULL DEFAULT 0,
  `parent_id` bigint(0) NULL DEFAULT NULL,
  `reply_id` bigint(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `html` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `comments` int(0) NULL DEFAULT 0,
  `support` int(0) NULL DEFAULT 0,
  `oppose` int(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `parent_id`(`parent_id`) USING BTREE,
  INDEX `answer_id`(`question_id`) USING BTREE,
  INDEX `is_deleted`(`is_deleted`) USING BTREE,
  INDEX `support`(`support`) USING BTREE,
  INDEX `oppose`(`oppose`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 108 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_comment_vote
-- ----------------------------
DROP TABLE IF EXISTS `question_comment_vote`;
CREATE TABLE `question_comment_vote`  (
  `type` int(0) NOT NULL DEFAULT 0,
  `comment_id` bigint(0) NOT NULL,
  `created_user_id` bigint(0) NOT NULL,
  `created_time` bigint(0) NOT NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`comment_id`, `created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_info
-- ----------------------------
DROP TABLE IF EXISTS `question_info`;
CREATE TABLE `question_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `reward_type` int(0) NULL DEFAULT 0 COMMENT '0 无悬赏 1积分 2现金',
  `reward_value` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '悬赏金额、积分',
  `reward_add` decimal(10, 2) NULL DEFAULT 0.00,
  `version` int(0) NULL DEFAULT 0 COMMENT '已发布版本',
  `is_published` tinyint(1) NULL DEFAULT 0 COMMENT '1已发布 0未发布',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `is_adopted` tinyint(1) NULL DEFAULT 2 COMMENT '1已采纳 2未采纳',
  `is_disabled` tinyint(1) NULL DEFAULT 1 COMMENT '1正常，2禁用',
  `draft` int(0) NULL DEFAULT 0,
  `adoption_id` bigint(0) NULL DEFAULT 0,
  `answers` int(0) NULL DEFAULT 0,
  `comments` int(0) NULL DEFAULT 0,
  `view` int(0) NOT NULL DEFAULT 0,
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `support` int(0) NULL DEFAULT 0,
  `oppose` int(0) NULL DEFAULT 0,
  `star_count` int(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`, `view`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE,
  INDEX `is_published`(`is_published`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 271 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_star
-- ----------------------------
DROP TABLE IF EXISTS `question_star`;
CREATE TABLE `question_star`  (
  `question_id` bigint(0) NOT NULL,
  `created_time` bigint(0) NOT NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NOT NULL,
  PRIMARY KEY (`question_id`, `created_user_id`) USING BTREE,
  INDEX `question_id`(`question_id`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_tag
-- ----------------------------
DROP TABLE IF EXISTS `question_tag`;
CREATE TABLE `question_tag`  (
  `question_id` bigint(0) NOT NULL,
  `tag_id` bigint(0) NOT NULL,
  PRIMARY KEY (`question_id`, `tag_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_version
-- ----------------------------
DROP TABLE IF EXISTS `question_version`;
CREATE TABLE `question_version`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `question_id` bigint(0) NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `html` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `version` int(0) NULL DEFAULT 0,
  `is_reviewed` tinyint(1) NULL DEFAULT 0 COMMENT '0 未审核 1 已审核',
  `is_disabled` tinyint(1) NULL DEFAULT 0 COMMENT '0 未禁用，1 禁用',
  `is_published` tinyint(1) NULL DEFAULT 0 COMMENT '0.未发布 1.发布',
  `save_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
  `reward_type` int(0) NULL DEFAULT 0 COMMENT '0 无悬赏 1积分 2现金',
  `reward_value` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '悬赏金额、积分',
  `reward_add` decimal(10, 2) NULL DEFAULT 0.00,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `question_id`(`question_id`, `version`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE,
  INDEX `question_id_2`(`question_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 988 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_version_tag
-- ----------------------------
DROP TABLE IF EXISTS `question_version_tag`;
CREATE TABLE `question_version_tag`  (
  `version` bigint(0) NOT NULL,
  `tag` bigint(0) NOT NULL,
  PRIMARY KEY (`version`, `tag`) USING BTREE,
  UNIQUE INDEX `question`(`version`, `tag`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for report_info
-- ----------------------------
DROP TABLE IF EXISTS `report_info`;
CREATE TABLE `report_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `action` int(0) NULL DEFAULT 0,
  `type` int(0) NULL DEFAULT 0,
  `state` int(0) NULL DEFAULT 0,
  `description` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `target_id` bigint(0) NULL DEFAULT 0,
  `target_user_id` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE,
  INDEX `target_id`(`target_id`, `action`, `type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for reward_config
-- ----------------------------
DROP TABLE IF EXISTS `reward_config`;
CREATE TABLE `reward_config`  (
  `id` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` int(0) NOT NULL DEFAULT 0,
  `min` decimal(10, 2) NULL DEFAULT 0.00,
  `max` decimal(10, 2) NULL DEFAULT 0.00,
  `unit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  PRIMARY KEY (`id`, `type`) USING BTREE,
  UNIQUE INDEX `key`(`id`, `type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for reward_log
-- ----------------------------
DROP TABLE IF EXISTS `reward_log`;
CREATE TABLE `reward_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `bank_type` int(0) NOT NULL DEFAULT 0,
  `data_type` int(0) NOT NULL DEFAULT 0,
  `data_key` bigint(0) NOT NULL DEFAULT 0,
  `amount` decimal(10, 2) UNSIGNED NOT NULL DEFAULT 0.00,
  `user_id` bigint(0) NOT NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for software_attributes
-- ----------------------------
DROP TABLE IF EXISTS `software_attributes`;
CREATE TABLE `software_attributes`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `software_id` bigint(0) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 314 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for software_comment
-- ----------------------------
DROP TABLE IF EXISTS `software_comment`;
CREATE TABLE `software_comment`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `software_id` bigint(0) NULL DEFAULT 0,
  `parent_id` bigint(0) NULL DEFAULT NULL,
  `reply_id` bigint(0) NULL DEFAULT NULL,
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `html` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `comment_count` int(0) NULL DEFAULT 0,
  `support` int(0) NULL DEFAULT 0,
  `oppose` int(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for software_comment_vote
-- ----------------------------
DROP TABLE IF EXISTS `software_comment_vote`;
CREATE TABLE `software_comment_vote`  (
  `type` int(0) NOT NULL DEFAULT 0,
  `comment_id` bigint(0) NOT NULL,
  `created_user_id` bigint(0) NOT NULL,
  `created_time` bigint(0) NOT NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`comment_id`, `created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for software_file
-- ----------------------------
DROP TABLE IF EXISTS `software_file`;
CREATE TABLE `software_file`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `updater_id` bigint(0) NOT NULL DEFAULT 0,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `suffix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `is_recommend` tinyint(1) NULL DEFAULT 0,
  `size` bigint(0) NOT NULL DEFAULT 0,
  `sha1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `sha256` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `md5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `ed2k` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `magnet` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `publish_date` bigint(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3099 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for software_grab
-- ----------------------------
DROP TABLE IF EXISTS `software_grab`;
CREATE TABLE `software_grab`  (
  `id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for software_group
-- ----------------------------
DROP TABLE IF EXISTS `software_group`;
CREATE TABLE `software_group`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for software_info
-- ----------------------------
DROP TABLE IF EXISTS `software_info`;
CREATE TABLE `software_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `group_id` int(0) NULL DEFAULT 0,
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `custom_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `is_published` tinyint(1) NULL DEFAULT 0 COMMENT '1已发布 0未发布',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `is_disabled` tinyint(1) NULL DEFAULT 1 COMMENT '1正常，2禁用',
  `version` int(0) NULL DEFAULT 0 COMMENT '已发布版本',
  `draft` int(0) NULL DEFAULT 0,
  `comment_count` int(0) NULL DEFAULT 0,
  `view` int(0) NULL DEFAULT 0,
  `support` int(0) NULL DEFAULT 0,
  `oppose` int(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 103 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for software_release
-- ----------------------------
DROP TABLE IF EXISTS `software_release`;
CREATE TABLE `software_release`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `software_id` bigint(0) NOT NULL,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 274 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for software_tag
-- ----------------------------
DROP TABLE IF EXISTS `software_tag`;
CREATE TABLE `software_tag`  (
  `software_id` bigint(0) NOT NULL,
  `tag_id` bigint(0) NOT NULL,
  PRIMARY KEY (`software_id`, `tag_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for software_updater
-- ----------------------------
DROP TABLE IF EXISTS `software_updater`;
CREATE TABLE `software_updater`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `release_id` bigint(0) NOT NULL,
  `name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 566 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for software_version
-- ----------------------------
DROP TABLE IF EXISTS `software_version`;
CREATE TABLE `software_version`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `software_id` bigint(0) NOT NULL,
  `group_id` bigint(0) NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `html` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `version` int(0) NULL DEFAULT 0,
  `is_reviewed` tinyint(1) NULL DEFAULT 0 COMMENT '0 未审核 1 已审核',
  `is_disabled` tinyint(1) NULL DEFAULT 0 COMMENT '0 未禁用，1 禁用',
  `is_published` tinyint(1) NULL DEFAULT 0 COMMENT '0.未发布 1.发布',
  `save_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 303 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for software_version_tag
-- ----------------------------
DROP TABLE IF EXISTS `software_version_tag`;
CREATE TABLE `software_version_tag`  (
  `version_id` bigint(0) NOT NULL,
  `tag_id` bigint(0) NOT NULL,
  PRIMARY KEY (`version_id`, `tag_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for software_vote
-- ----------------------------
DROP TABLE IF EXISTS `software_vote`;
CREATE TABLE `software_vote`  (
  `type` int(0) NOT NULL DEFAULT 0,
  `software_id` bigint(0) NOT NULL,
  `created_user_id` bigint(0) NOT NULL,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`software_id`, `created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_link
-- ----------------------------
DROP TABLE IF EXISTS `sys_link`;
CREATE TABLE `sys_link`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `text` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `link` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `target` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '_blank',
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_path
-- ----------------------------
DROP TABLE IF EXISTS `sys_path`;
CREATE TABLE `sys_path`  (
  `path` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `type` int(0) NOT NULL DEFAULT 0,
  `id` bigint(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`path`) USING BTREE,
  INDEX `type`(`type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `name` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '权限名称',
  `platform` int(0) NULL DEFAULT 0 COMMENT '权限平台，api/web/admin',
  `type` int(0) NULL DEFAULT 0 COMMENT '权限类型，比如 url，button',
  `method` int(0) NULL DEFAULT 0 COMMENT '权限请求/操作方式：post，click ',
  `data` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '权限数据，例如：url，',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  PRIMARY KEY (`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` tinyint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `disabled` tinyint(1) NULL DEFAULT 0,
  `system` tinyint(1) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT NULL,
  `created_time` bigint(0) NULL DEFAULT NULL,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`, `created_user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `role_id` bigint(0) NOT NULL DEFAULT 0 COMMENT '角色编号',
  `permission_name` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `created_user_id` bigint(0) NULL DEFAULT NULL,
  `created_time` bigint(0) NULL DEFAULT NULL,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`role_id`, `permission_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_setting
-- ----------------------------
DROP TABLE IF EXISTS `sys_setting`;
CREATE TABLE `sys_setting`  (
  `key` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'default',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ITELLYOU',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `icp_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `copyright` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `company_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `user_agreement_link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `footer_scripts` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `group_id` bigint(0) NULL DEFAULT NULL,
  `star_count` int(0) NULL DEFAULT 0,
  `article_count` int(0) NULL DEFAULT NULL,
  `question_count` int(0) NULL DEFAULT NULL,
  `version` int(0) NULL DEFAULT 0,
  `draft` int(0) NULL DEFAULT 0,
  `is_published` tinyint(1) NULL DEFAULT 0 COMMENT '1已发布 0未发布',
  `is_disabled` tinyint(1) NULL DEFAULT 0 COMMENT '0正常，1禁用',
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `tag_name`(`name`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE,
  INDEX `group_id`(`group_id`) USING BTREE,
  INDEX `is_published`(`is_published`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 116 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tag_group
-- ----------------------------
DROP TABLE IF EXISTS `tag_group`;
CREATE TABLE `tag_group`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `tag_count` int(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tag_star
-- ----------------------------
DROP TABLE IF EXISTS `tag_star`;
CREATE TABLE `tag_star`  (
  `tag_id` bigint(0) NOT NULL,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NOT NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`tag_id`, `created_user_id`) USING BTREE,
  INDEX `user_id`(`tag_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tag_version
-- ----------------------------
DROP TABLE IF EXISTS `tag_version`;
CREATE TABLE `tag_version`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `tag_id` bigint(0) NULL DEFAULT NULL,
  `version` int(0) NULL DEFAULT 0,
  `is_reviewed` tinyint(1) NULL DEFAULT 0 COMMENT '0 未审核 1 已审核',
  `is_disabled` tinyint(1) NULL DEFAULT 0 COMMENT '0 未禁用，1 禁用',
  `is_published` tinyint(1) NULL DEFAULT 0 COMMENT '0.未发布 1.发布',
  `icon` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `html` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `description` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `save_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `created_user_id` bigint(0) NULL DEFAULT NULL,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `tag_id_2`(`tag_id`, `version`) USING BTREE,
  INDEX `tag_id`(`tag_id`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 269 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for upload_config
-- ----------------------------
DROP TABLE IF EXISTS `upload_config`;
CREATE TABLE `upload_config`  (
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `bucket` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `domain` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `endpoint` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `size` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for upload_file
-- ----------------------------
DROP TABLE IF EXISTS `upload_file`;
CREATE TABLE `upload_file`  (
  `key` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `bucket` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `extname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `source` int(0) NULL DEFAULT 0,
  `size` bigint(0) NULL DEFAULT NULL,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT NULL,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`key`, `bucket`) USING BTREE,
  INDEX `extname`(`extname`(191)) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for upload_file_config
-- ----------------------------
DROP TABLE IF EXISTS `upload_file_config`;
CREATE TABLE `upload_file_config`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `is_image` tinyint(1) NULL DEFAULT 0,
  `is_video` tinyint(1) NULL DEFAULT 0,
  `is_file` tinyint(1) NULL DEFAULT 1,
  `is_doc` tinyint(1) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT NULL,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 69 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_activity
-- ----------------------------
DROP TABLE IF EXISTS `user_activity`;
CREATE TABLE `user_activity`  (
  `type` int(0) NOT NULL DEFAULT 0,
  `action` int(0) NOT NULL DEFAULT 0,
  `target_id` bigint(0) NOT NULL DEFAULT 0,
  `target_user_id` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NOT NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`type`, `action`, `target_id`, `created_user_id`) USING BTREE,
  INDEX `type`(`type`, `action`) USING BTREE,
  INDEX `target_user_id`(`target_user_id`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_bank
-- ----------------------------
DROP TABLE IF EXISTS `user_bank`;
CREATE TABLE `user_bank`  (
  `user_id` bigint(0) NOT NULL,
  `cash` decimal(10, 2) UNSIGNED NOT NULL DEFAULT 0.00,
  `credit` int(0) UNSIGNED NOT NULL DEFAULT 0,
  `score` int(0) NULL DEFAULT 0,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_bank_config
-- ----------------------------
DROP TABLE IF EXISTS `user_bank_config`;
CREATE TABLE `user_bank_config`  (
  `bank_type` int(0) NOT NULL DEFAULT 0,
  `action` int(0) NOT NULL DEFAULT 0 COMMENT '操作类型',
  `type` int(0) NOT NULL DEFAULT 0 COMMENT '操作对象',
  `targeter_step` int(0) NOT NULL DEFAULT 0 COMMENT '有操作对象的情况下，操作对象的用户所加的分数',
  `creater_step` int(0) NOT NULL DEFAULT 0 COMMENT '操作者所加的分数',
  `creater_min_score` int(0) NULL DEFAULT 0 COMMENT '操作者最低需要多少分数触发操作后，才能让操作对象的用户获得积分',
  `targeter_count_of_day` int(0) NULL DEFAULT 0 COMMENT '操作者对象用户一天能获得几次积分',
  `targeter_total_of_day` int(0) NULL DEFAULT 0 COMMENT '操作者对象用户一天最多能获得多少积分',
  `targeter_count_of_week` int(0) NULL DEFAULT 0 COMMENT '操作者对象用户一周能获得几次积分',
  `targeter_total_of_week` int(0) NULL DEFAULT 0 COMMENT '操作者对象用户一周最多能获得多少积分',
  `targeter_count_of_month` int(0) NULL DEFAULT 0 COMMENT '操作者对象用户一个月能获得几次积分',
  `targeter_total_of_month` int(0) NULL DEFAULT 0 COMMENT '操作者对象用户一个月最多能获得多少积分',
  `creater_count_of_day` int(0) NULL DEFAULT 0 COMMENT '操作者一天能获得几次积分',
  `creater_total_of_day` int(0) NULL DEFAULT 0 COMMENT '操作者一天最多能获得多少积分',
  `creater_count_of_week` int(0) NULL DEFAULT 0 COMMENT '操作者一周能获得几次积分',
  `creater_total_of_week` int(0) NULL DEFAULT 0 COMMENT '操作者一周最多能获得多少积分',
  `creater_count_of_month` int(0) NULL DEFAULT 0 COMMENT '操作者一个月能获得几次积分',
  `creater_total_of_month` int(0) NULL DEFAULT 0 COMMENT '操作者一个月最多能获得多少积分',
  `targeter_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注说明',
  `creater_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注说明',
  `only_once` tinyint(0) NULL DEFAULT 0,
  PRIMARY KEY (`bank_type`, `action`, `type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_bank_log
-- ----------------------------
DROP TABLE IF EXISTS `user_bank_log`;
CREATE TABLE `user_bank_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `amount` decimal(10, 2) NULL DEFAULT NULL,
  `action` int(0) NULL DEFAULT 0,
  `type` int(0) NULL DEFAULT 0 COMMENT '1.积分，2.金钱，3.分数',
  `balance` decimal(10, 2) NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `data_type` int(0) NULL DEFAULT 0 COMMENT '1.提问悬赏',
  `data_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `data_type`(`data_type`, `data_key`(191)) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 535 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_draft
-- ----------------------------
DROP TABLE IF EXISTS `user_draft`;
CREATE TABLE `user_draft`  (
  `data_type` int(0) NOT NULL,
  `data_key` bigint(0) NOT NULL,
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `author_id` bigint(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  `created_user_id` bigint(0) NOT NULL,
  PRIMARY KEY (`data_type`, `data_key`, `created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `login_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `login_password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '登录密码',
  `pay_password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '支付密码',
  `name` varchar(180) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `gender` int(0) NULL DEFAULT 0 COMMENT '0.保密 1.男性 2.女性',
  `birthday` bigint(0) NULL DEFAULT 0,
  `mobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '',
  `mobile_status` tinyint(1) NULL DEFAULT 0,
  `email` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '',
  `email_status` tinyint(1) NULL DEFAULT 0,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `introduction` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `profession` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `is_disabled` tinyint(1) NULL DEFAULT 0,
  `star_count` int(0) NULL DEFAULT 0,
  `follower_count` int(0) NULL DEFAULT 0,
  `question_count` int(0) NULL DEFAULT 0,
  `answer_count` int(0) NULL DEFAULT 0,
  `article_count` int(0) NULL DEFAULT 0,
  `column_count` int(0) NULL DEFAULT 0,
  `collection_count` int(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_name`(`login_name`) USING BTREE,
  UNIQUE INDEX `mobile_number`(`mobile`) USING BTREE,
  UNIQUE INDEX `email_address`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13023 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_login_log
-- ----------------------------
DROP TABLE IF EXISTS `user_login_log`;
CREATE TABLE `user_login_log`  (
  `token` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_disabled` tinyint(1) NULL DEFAULT 0,
  `client_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'unknown',
  `created_user_id` bigint(0) NOT NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  `created_time` bigint(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`token`) USING BTREE,
  UNIQUE INDEX `token`(`token`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_notification
-- ----------------------------
DROP TABLE IF EXISTS `user_notification`;
CREATE TABLE `user_notification`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `is_read` tinyint(1) NULL DEFAULT 0,
  `is_deleted` tinyint(1) NULL DEFAULT 0,
  `receive_id` bigint(0) NULL DEFAULT 0,
  `type` int(0) NULL DEFAULT 0,
  `action` int(0) NULL DEFAULT 0,
  `target_id` bigint(0) NULL DEFAULT 0,
  `merge_count` int(0) NULL DEFAULT 1,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `type`(`type`, `action`) USING BTREE,
  INDEX `receive_id`(`receive_id`) USING BTREE,
  INDEX `target_id`(`target_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 162 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_notification_actors
-- ----------------------------
DROP TABLE IF EXISTS `user_notification_actors`;
CREATE TABLE `user_notification_actors`  (
  `notification_id` bigint(0) NOT NULL DEFAULT 0,
  `user_id` bigint(0) NOT NULL DEFAULT 0,
  `target_id` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`notification_id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_notification_display
-- ----------------------------
DROP TABLE IF EXISTS `user_notification_display`;
CREATE TABLE `user_notification_display`  (
  `user_id` bigint(0) NOT NULL,
  `action` int(0) NOT NULL,
  `type` int(0) NOT NULL,
  `value` int(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`, `action`, `type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_notification_display_default
-- ----------------------------
DROP TABLE IF EXISTS `user_notification_display_default`;
CREATE TABLE `user_notification_display_default`  (
  `action` int(0) NOT NULL,
  `type` int(0) NOT NULL,
  `value` int(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`action`, `type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_notification_mark
-- ----------------------------
DROP TABLE IF EXISTS `user_notification_mark`;
CREATE TABLE `user_notification_mark`  (
  `user_id` bigint(0) NOT NULL,
  `updated_time` bigint(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_notification_queue
-- ----------------------------
DROP TABLE IF EXISTS `user_notification_queue`;
CREATE TABLE `user_notification_queue`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `type` int(0) NULL DEFAULT 0,
  `action` int(0) NULL DEFAULT 0,
  `target_id` bigint(0) NULL DEFAULT 0,
  `target_user_id` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `type`(`type`, `action`) USING BTREE,
  INDEX `target_user_id`(`target_user_id`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 410 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_operational
-- ----------------------------
DROP TABLE IF EXISTS `user_operational`;
CREATE TABLE `user_operational`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `type` int(0) NULL DEFAULT 0,
  `action` int(0) NULL DEFAULT 0,
  `target_id` bigint(0) NULL DEFAULT 0,
  `target_user_id` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `type`(`type`, `action`) USING BTREE,
  INDEX `target_user_id`(`target_user_id`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22324 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_payment
-- ----------------------------
DROP TABLE IF EXISTS `user_payment`;
CREATE TABLE `user_payment`  (
  `id` varchar(171) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `amount` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `type` int(0) NOT NULL DEFAULT 0,
  `subject` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `status` int(0) NOT NULL DEFAULT 0,
  `created_user_id` bigint(0) NOT NULL DEFAULT 0,
  `created_ip` bigint(0) NOT NULL DEFAULT 0,
  `created_time` bigint(0) NOT NULL DEFAULT 0,
  `updated_user_id` bigint(0) NOT NULL DEFAULT 0,
  `updated_ip` bigint(0) NOT NULL DEFAULT 0,
  `updated_time` bigint(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `status`(`status`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE,
  INDEX `status_2`(`status`) USING BTREE,
  INDEX `created_user_id_2`(`created_user_id`) USING BTREE,
  INDEX `status_3`(`status`) USING BTREE,
  INDEX `created_user_id_3`(`created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_rank
-- ----------------------------
DROP TABLE IF EXISTS `user_rank`;
CREATE TABLE `user_rank`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `min_score` int(0) NULL DEFAULT 0 COMMENT '最小权限分',
  `max_score` int(0) NULL DEFAULT 0 COMMENT '最大权限分',
  `created_user_id` bigint(0) NULL DEFAULT NULL,
  `created_time` bigint(0) NULL DEFAULT NULL,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_rank_role
-- ----------------------------
DROP TABLE IF EXISTS `user_rank_role`;
CREATE TABLE `user_rank_role`  (
  `rank_id` bigint(0) NOT NULL,
  `role_id` bigint(0) NOT NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT NULL,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`rank_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `user_id` bigint(0) NOT NULL,
  `role_id` bigint(0) NOT NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT NULL,
  `created_time` bigint(0) NULL DEFAULT NULL,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_star
-- ----------------------------
DROP TABLE IF EXISTS `user_star`;
CREATE TABLE `user_star`  (
  `user_id` bigint(0) NOT NULL,
  `created_time` bigint(0) NOT NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NOT NULL,
  PRIMARY KEY (`user_id`, `created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_third_account
-- ----------------------------
DROP TABLE IF EXISTS `user_third_account`;
CREATE TABLE `user_third_account`  (
  `user_id` bigint(0) NOT NULL,
  `type` int(0) NOT NULL,
  `key` varchar(171) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `home` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `star` bigint(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT NULL,
  `created_ip` bigint(0) NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`, `type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_third_log
-- ----------------------------
DROP TABLE IF EXISTS `user_third_log`;
CREATE TABLE `user_third_log`  (
  `id` varchar(171) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `type` int(0) NOT NULL,
  `action` int(0) NOT NULL,
  `is_verify` tinyint(1) NOT NULL DEFAULT 0,
  `redirect_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '',
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_verify
-- ----------------------------
DROP TABLE IF EXISTS `user_verify`;
CREATE TABLE `user_verify`  (
  `key` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `is_disabled` tinyint(1) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`key`) USING BTREE,
  INDEX `created_user_id`(`created_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_withdraw
-- ----------------------------
DROP TABLE IF EXISTS `user_withdraw`;
CREATE TABLE `user_withdraw`  (
  `id` varchar(171) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `amount` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `subject` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '提现',
  `commissionCharge` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `status` int(0) NOT NULL DEFAULT 0,
  `created_user_id` bigint(0) NOT NULL DEFAULT 0,
  `created_ip` bigint(0) NOT NULL DEFAULT 0,
  `created_time` bigint(0) NOT NULL DEFAULT 0,
  `updated_user_id` bigint(0) NOT NULL DEFAULT 0,
  `updated_ip` bigint(0) NOT NULL DEFAULT 0,
  `updated_time` bigint(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_withdraw_config
-- ----------------------------
DROP TABLE IF EXISTS `user_withdraw_config`;
CREATE TABLE `user_withdraw_config`  (
  `id` varchar(171) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `min` decimal(10, 2) NOT NULL DEFAULT 10.00 COMMENT '单次最低取现额度',
  `max` decimal(10, 2) NOT NULL DEFAULT 2000.00 COMMENT '单次最大取现额度',
  `auto` decimal(10, 2) NOT NULL DEFAULT 200.00 COMMENT '支付宝单次自动提现最高额度，也就是需要人工审核的最低额度',
  `rate` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '提现手续费率',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for view_info
-- ----------------------------
DROP TABLE IF EXISTS `view_info`;
CREATE TABLE `view_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '',
  `data_type` int(0) NULL DEFAULT NULL,
  `data_key` bigint(0) NULL DEFAULT NULL,
  `os` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '',
  `created_user_id` bigint(0) NULL DEFAULT 0,
  `created_time` bigint(0) NULL DEFAULT 0,
  `created_ip` bigint(0) NULL DEFAULT 0,
  `updated_time` bigint(0) NULL DEFAULT 0,
  `updated_ip` bigint(0) NULL DEFAULT 0,
  `updated_user_id` bigint(0) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `data_type`(`data_type`, `data_key`, `created_user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23632 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
