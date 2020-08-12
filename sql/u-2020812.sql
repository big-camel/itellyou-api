/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80020
 Source Host           : localhost:3306
 Source Schema         : itellyou

 Target Server Type    : MySQL
 Target Server Version : 80020
 File Encoding         : 65001

 Date: 12/08/2020 15:38:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
) ENGINE = InnoDB AUTO_INCREMENT = 266 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 2772 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 87 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 231 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 478 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 84 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

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

SET FOREIGN_KEY_CHECKS = 1;
