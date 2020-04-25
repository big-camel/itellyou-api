/*
 Navicat MySQL Data Transfer

 Source Server         : itellyou
 Source Server Type    : MySQL
 Source Server Version : 50616
 Source Host           : itellyou.mysql.rds.aliyuncs.com
 Source Database       : itellyou

 Target Server Type    : MySQL
 Target Server Version : 50616
 File Encoding         : utf-8

 Date: 04/17/2020 00:05:23 AM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `ali_config`
-- ----------------------------
DROP TABLE IF EXISTS `ali_config`;
CREATE TABLE `ali_config` (
  `id` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `secret` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `ali_dm_config`
-- ----------------------------
DROP TABLE IF EXISTS `ali_dm_config`;
CREATE TABLE `ali_dm_config` (
  `type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `minute` int(11) NOT NULL DEFAULT '1',
  `hour` int(11) NOT NULL DEFAULT '5',
  `day` int(11) NOT NULL DEFAULT '10',
  PRIMARY KEY (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Records of `ali_dm_config`
-- ----------------------------
BEGIN;
INSERT INTO `ali_dm_config` VALUES ('email', '2', '10', '20'), ('ip', '4', '20', '40');
COMMIT;

-- ----------------------------
--  Table structure for `ali_dm_log`
-- ----------------------------
DROP TABLE IF EXISTS `ali_dm_log`;
CREATE TABLE `ali_dm_log` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `template_id` varchar(191) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `data` varchar(191) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint(1) DEFAULT '0',
  `created_time` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=171 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `ali_dm_template`
-- ----------------------------
DROP TABLE IF EXISTS `ali_dm_template`;
CREATE TABLE `ali_dm_template` (
  `id` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `name` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `param` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `send_addr` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `send_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `title` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `body` text COLLATE utf8mb4_unicode_ci,
  `tag_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `expire` int(11) DEFAULT '3600',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Records of `ali_dm_template`
-- ----------------------------
BEGIN;
INSERT INTO `ali_dm_template` VALUES ('replace', '更换邮箱验证码模版', ' {\"code\":\"${code}\"}', 'info@service.itellyou.com', 'ITELLYOU', 'ITELLYOU 验证码', '<p>本次变更邮箱需要的验证码是 <strong>${code}</strong>，请输入验证码进行下一步操作。</p>', '', '900'), ('verify', '安全验证验证码模版', ' {\"code\":\"${code}\"}', 'info@service.itellyou.com', 'ITELLYOU', 'ITELLYOU 验证码', '<p>本次验证账号安全需要的验证码是 <strong>${code}</strong>，请输入验证码进行下一步操作。</p>', '', '900');
COMMIT;

-- ----------------------------
--  Table structure for `ali_sms_config`
-- ----------------------------
DROP TABLE IF EXISTS `ali_sms_config`;
CREATE TABLE `ali_sms_config` (
  `type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `minute` int(11) NOT NULL DEFAULT '1',
  `hour` int(11) NOT NULL DEFAULT '5',
  `day` int(11) NOT NULL DEFAULT '10',
  PRIMARY KEY (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Records of `ali_sms_config`
-- ----------------------------
BEGIN;
INSERT INTO `ali_sms_config` VALUES ('ip', '4', '20', '40'), ('mobile', '2', '10', '20');
COMMIT;

-- ----------------------------
--  Table structure for `ali_sms_log`
-- ----------------------------
DROP TABLE IF EXISTS `ali_sms_log`;
CREATE TABLE `ali_sms_log` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `template_id` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `data` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` tinyint(1) DEFAULT '0',
  `created_time` bigint(20) NOT NULL DEFAULT '0',
  `created_ip` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `mobile` (`mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=190 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `ali_sms_template`
-- ----------------------------
DROP TABLE IF EXISTS `ali_sms_template`;
CREATE TABLE `ali_sms_template` (
  `id` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `name` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `code` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `param` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `sign_name` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `expire` int(11) NOT NULL DEFAULT '3600',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Records of `ali_sms_template`
-- ----------------------------
BEGIN;
INSERT INTO `ali_sms_template` VALUES ('login', '登录验证码模版', 'SMS_86860054', ' {\"code\":\"${code}\"}', 'itellyou', '900'), ('register', '注册验证码模版', 'SMS_86860052', ' {\"code\":\"${code}\"}', 'itellyou', '900'), ('replace', '替换手机验证码模版', 'SMS_86860050', ' {\"code\":\"${code}\"}', 'itellyou', '900'), ('verify', '安全验证验证码模版', 'SMS_86860056', ' {\"code\":\"${code}\"}', 'itellyou', '900');
COMMIT;

-- ----------------------------
--  Table structure for `alipay_config`
-- ----------------------------
DROP TABLE IF EXISTS `alipay_config`;
CREATE TABLE `alipay_config` (
  `app_id` varchar(180) NOT NULL DEFAULT '',
  `private_key` varchar(500) DEFAULT '',
  `public_key` varchar(500) DEFAULT '',
  `gateway` varchar(255) DEFAULT '',
  `notify_url` varchar(255) DEFAULT NULL,
  `return_url` varchar(255) DEFAULT NULL,
  `is_enable` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `article_comment`
-- ----------------------------
DROP TABLE IF EXISTS `article_comment`;
CREATE TABLE `article_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) DEFAULT '0',
  `parent_id` bigint(20) DEFAULT NULL,
  `reply_id` bigint(20) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `content` text,
  `html` text,
  `comment_count` int(11) DEFAULT '0',
  `support` int(11) DEFAULT '0',
  `oppose` int(11) DEFAULT '0',
  `created_time` bigint(20) DEFAULT '0',
  `created_user_id` bigint(11) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT NULL,
  `updated_time` bigint(20) DEFAULT '0',
  `updated_user_id` bigint(11) DEFAULT '0',
  `updated_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `article_id` (`article_id`),
  KEY `parent_id` (`parent_id`),
  KEY `reply_id` (`reply_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `article_comment_vote`
-- ----------------------------
DROP TABLE IF EXISTS `article_comment_vote`;
CREATE TABLE `article_comment_vote` (
  `type` int(4) NOT NULL DEFAULT '0',
  `comment_id` bigint(20) NOT NULL,
  `created_user_id` bigint(20) NOT NULL,
  `created_time` bigint(20) NOT NULL DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  PRIMARY KEY (`comment_id`,`created_user_id`),
  KEY `type` (`type`,`comment_id`),
  KEY `created_user_id` (`created_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `article_info`
-- ----------------------------
DROP TABLE IF EXISTS `article_info`;
CREATE TABLE `article_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` int(11) DEFAULT '0' COMMENT '已发布版本',
  `is_published` tinyint(1) DEFAULT '0' COMMENT '1已发布 0未发布',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `is_disabled` tinyint(1) DEFAULT '1' COMMENT '1正常，2禁用',
  `draft` int(11) DEFAULT '0',
  `comment_count` int(11) DEFAULT '0',
  `view` int(11) DEFAULT '0',
  `support` int(11) DEFAULT '0',
  `oppose` int(11) DEFAULT '0',
  `star_count` int(11) DEFAULT '0',
  `created_time` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT NULL,
  `updated_time` bigint(20) DEFAULT '0',
  `updated_user_id` bigint(20) DEFAULT '0',
  `updated_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `is_published` (`is_published`),
  KEY `created_user_id` (`created_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `article_star`
-- ----------------------------
DROP TABLE IF EXISTS `article_star`;
CREATE TABLE `article_star` (
  `article_id` bigint(20) NOT NULL,
  `created_time` bigint(20) NOT NULL DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`article_id`,`created_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `article_version`
-- ----------------------------
DROP TABLE IF EXISTS `article_version`;
CREATE TABLE `article_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) NOT NULL,
  `column_id` bigint(20) DEFAULT NULL,
  `source_type` int(2) DEFAULT '1',
  `source_data` varchar(500) DEFAULT '',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `html` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
  `version` int(11) DEFAULT '0',
  `is_reviewed` tinyint(1) DEFAULT '0' COMMENT '0 未审核 1 已审核',
  `is_disabled` tinyint(1) DEFAULT '0' COMMENT '0 未禁用，1 禁用',
  `is_published` tinyint(1) DEFAULT '0' COMMENT '0.未发布 1.发布',
  `save_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '备注',
  `created_time` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT NULL,
  `updated_time` bigint(20) DEFAULT '0',
  `updated_user_id` bigint(20) DEFAULT '0',
  `updated_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `article_id_2` (`article_id`,`version`) USING BTREE,
  KEY `column_id` (`column_id`),
  KEY `article_id` (`article_id`)
) ENGINE=InnoDB AUTO_INCREMENT=359 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `article_version_tag`
-- ----------------------------
DROP TABLE IF EXISTS `article_version_tag`;
CREATE TABLE `article_version_tag` (
  `version` bigint(20) NOT NULL,
  `tag` bigint(20) NOT NULL,
  PRIMARY KEY (`version`,`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `article_vote`
-- ----------------------------
DROP TABLE IF EXISTS `article_vote`;
CREATE TABLE `article_vote` (
  `type` int(4) NOT NULL DEFAULT '0',
  `article_id` bigint(20) NOT NULL,
  `created_user_id` bigint(20) NOT NULL,
  `created_time` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`article_id`,`created_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `collab_config`
-- ----------------------------
DROP TABLE IF EXISTS `collab_config`;
CREATE TABLE `collab_config` (
  `key` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL,
  `host` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Records of `collab_config`
-- ----------------------------
BEGIN;
INSERT INTO `collab_config` VALUES ('default', 'ws://localhost:8082');
COMMIT;

-- ----------------------------
--  Table structure for `collab_info`
-- ----------------------------
DROP TABLE IF EXISTS `collab_info`;
CREATE TABLE `collab_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `token` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `host` varchar(191) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_disabled` tinyint(1) DEFAULT '0',
  `created_user_id` bigint(20) DEFAULT NULL,
  `created_time` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`) USING BTREE,
  KEY `key` (`key`)
) ENGINE=InnoDB AUTO_INCREMENT=558 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `column_info`
-- ----------------------------
DROP TABLE IF EXISTS `column_info`;
CREATE TABLE `column_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(191) NOT NULL DEFAULT '',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
  `description` varchar(2000) DEFAULT '',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `is_disabled` tinyint(1) DEFAULT '1' COMMENT '1正常，2禁用',
  `is_reviewed` tinyint(1) DEFAULT '0' COMMENT '0 未审核 1 已审核',
  `member_count` int(11) DEFAULT '0',
  `article_count` int(11) DEFAULT '0',
  `star_count` int(11) DEFAULT '0',
  `created_time` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT NULL,
  `updated_time` bigint(20) DEFAULT '0',
  `updated_user_id` bigint(20) DEFAULT '0',
  `updated_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `created_user_id` (`created_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1004 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `column_member`
-- ----------------------------
DROP TABLE IF EXISTS `column_member`;
CREATE TABLE `column_member` (
  `column_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `created_time` bigint(20) NOT NULL DEFAULT '0',
  `created_ip` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`column_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `column_star`
-- ----------------------------
DROP TABLE IF EXISTS `column_star`;
CREATE TABLE `column_star` (
  `column_id` bigint(20) NOT NULL,
  `created_time` bigint(20) NOT NULL DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`column_id`,`created_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `column_tag`
-- ----------------------------
DROP TABLE IF EXISTS `column_tag`;
CREATE TABLE `column_tag` (
  `column_id` bigint(20) NOT NULL,
  `tag_id` bigint(20) NOT NULL,
  PRIMARY KEY (`column_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `geetest_config`
-- ----------------------------
DROP TABLE IF EXISTS `geetest_config`;
CREATE TABLE `geetest_config` (
  `id` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL,
  `key` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `geetest_log`
-- ----------------------------
DROP TABLE IF EXISTS `geetest_log`;
CREATE TABLE `geetest_log` (
  `key` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
  `client_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `client_ip` bigint(20) NOT NULL DEFAULT '0',
  `status` tinyint(1) NOT NULL DEFAULT '1',
  `mode` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'unknow',
  `created_user_id` bigint(20) NOT NULL DEFAULT '0',
  `created_time` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`key`),
  KEY `created_user_id` (`created_user_id`),
  KEY `mode` (`mode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `question_answer`
-- ----------------------------
DROP TABLE IF EXISTS `question_answer`;
CREATE TABLE `question_answer` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `question_id` bigint(11) DEFAULT NULL,
  `version` int(11) DEFAULT '0',
  `draft` int(11) DEFAULT NULL,
  `is_published` tinyint(1) DEFAULT '0' COMMENT '1已发布 0未发布',
  `is_disabled` tinyint(1) DEFAULT '1' COMMENT '1正常，2禁用',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `is_adopted` tinyint(1) DEFAULT NULL,
  `comments` int(11) DEFAULT '0',
  `support` int(11) DEFAULT '0',
  `oppose` int(11) DEFAULT '0',
  `view` int(11) DEFAULT '0',
  `star_count` int(11) DEFAULT '0',
  `created_time` bigint(20) DEFAULT '0',
  `created_user_id` bigint(11) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT NULL,
  `updated_time` bigint(20) DEFAULT '0',
  `updated_user_id` bigint(11) DEFAULT '0',
  `updated_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `question_id` (`question_id`),
  KEY `is_published` (`is_published`),
  KEY `created_user_id` (`created_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `question_answer_comment`
-- ----------------------------
DROP TABLE IF EXISTS `question_answer_comment`;
CREATE TABLE `question_answer_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `answer_id` bigint(20) DEFAULT '0',
  `parent_id` bigint(20) DEFAULT NULL,
  `reply_id` bigint(20) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `content` text,
  `html` text,
  `comments` int(11) DEFAULT '0',
  `support` int(11) DEFAULT '0',
  `oppose` int(11) DEFAULT '0',
  `created_time` bigint(20) DEFAULT '0',
  `created_user_id` bigint(11) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT NULL,
  `updated_time` bigint(20) DEFAULT '0',
  `updated_user_id` bigint(11) DEFAULT '0',
  `updated_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `parent_id` (`parent_id`),
  KEY `answer_id` (`answer_id`),
  KEY `is_deleted` (`is_deleted`),
  KEY `support` (`support`),
  KEY `oppose` (`oppose`),
  KEY `created_user_id` (`created_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `question_answer_comment_vote`
-- ----------------------------
DROP TABLE IF EXISTS `question_answer_comment_vote`;
CREATE TABLE `question_answer_comment_vote` (
  `type` int(4) NOT NULL DEFAULT '0',
  `comment_id` bigint(20) NOT NULL,
  `created_user_id` bigint(20) NOT NULL,
  `created_time` bigint(20) NOT NULL DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  PRIMARY KEY (`comment_id`,`created_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `question_answer_star`
-- ----------------------------
DROP TABLE IF EXISTS `question_answer_star`;
CREATE TABLE `question_answer_star` (
  `answer_id` bigint(20) NOT NULL,
  `created_time` bigint(20) NOT NULL DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`answer_id`,`created_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `question_answer_version`
-- ----------------------------
DROP TABLE IF EXISTS `question_answer_version`;
CREATE TABLE `question_answer_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `answer_id` bigint(11) NOT NULL DEFAULT '0',
  `is_reviewed` tinyint(1) DEFAULT '0' COMMENT '0 未审核 1 已审核',
  `is_disabled` tinyint(1) DEFAULT '0' COMMENT '0 未禁用，1 禁用',
  `is_published` tinyint(1) DEFAULT '0' COMMENT '0.未发布 1.发布',
  `content` text,
  `html` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `version` int(11) DEFAULT '0',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '备注',
  `save_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
  `created_time` bigint(20) DEFAULT '0',
  `created_user_id` bigint(11) DEFAULT NULL,
  `created_ip` bigint(255) DEFAULT NULL,
  `updated_time` bigint(20) DEFAULT '0',
  `updated_user_id` bigint(11) DEFAULT '0',
  `updated_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `comment_id` (`answer_id`,`version`),
  KEY `answer_id` (`answer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `question_answer_vote`
-- ----------------------------
DROP TABLE IF EXISTS `question_answer_vote`;
CREATE TABLE `question_answer_vote` (
  `type` int(4) NOT NULL DEFAULT '0',
  `answer_id` bigint(20) NOT NULL,
  `created_user_id` bigint(20) NOT NULL,
  `created_time` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`answer_id`,`created_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `question_comment`
-- ----------------------------
DROP TABLE IF EXISTS `question_comment`;
CREATE TABLE `question_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `question_id` bigint(20) DEFAULT '0',
  `parent_id` bigint(20) DEFAULT NULL,
  `reply_id` bigint(20) DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `content` text,
  `html` text,
  `comments` int(11) DEFAULT '0',
  `support` int(11) DEFAULT '0',
  `oppose` int(11) DEFAULT '0',
  `created_time` bigint(20) DEFAULT '0',
  `created_user_id` bigint(11) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT NULL,
  `updated_time` bigint(20) DEFAULT '0',
  `updated_user_id` bigint(11) DEFAULT '0',
  `updated_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `parent_id` (`parent_id`),
  KEY `answer_id` (`question_id`),
  KEY `is_deleted` (`is_deleted`),
  KEY `support` (`support`),
  KEY `oppose` (`oppose`),
  KEY `created_user_id` (`created_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `question_comment_vote`
-- ----------------------------
DROP TABLE IF EXISTS `question_comment_vote`;
CREATE TABLE `question_comment_vote` (
  `type` int(4) NOT NULL DEFAULT '0',
  `comment_id` bigint(20) NOT NULL,
  `created_user_id` bigint(20) NOT NULL,
  `created_time` bigint(20) NOT NULL DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  PRIMARY KEY (`comment_id`,`created_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `question_info`
-- ----------------------------
DROP TABLE IF EXISTS `question_info`;
CREATE TABLE `question_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` int(11) DEFAULT '0' COMMENT '已发布版本',
  `is_published` tinyint(1) DEFAULT '0' COMMENT '1已发布 0未发布',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `is_adopted` tinyint(1) DEFAULT '2' COMMENT '1已采纳 2未采纳',
  `is_disabled` tinyint(1) DEFAULT '1' COMMENT '1正常，2禁用',
  `draft` int(11) DEFAULT '0',
  `adoption_id` bigint(20) DEFAULT '0',
  `answers` int(11) DEFAULT '0',
  `comments` int(11) DEFAULT '0',
  `view` int(11) NOT NULL DEFAULT '0',
  `support` int(11) DEFAULT '0',
  `oppose` int(11) DEFAULT '0',
  `star_count` int(11) DEFAULT '0',
  `created_time` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT NULL,
  `updated_time` bigint(20) DEFAULT '0',
  `updated_user_id` bigint(20) DEFAULT '0',
  `updated_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`view`),
  KEY `created_user_id` (`created_user_id`) USING BTREE,
  KEY `is_published` (`is_published`)
) ENGINE=InnoDB AUTO_INCREMENT=178 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `question_star`
-- ----------------------------
DROP TABLE IF EXISTS `question_star`;
CREATE TABLE `question_star` (
  `question_id` bigint(20) NOT NULL,
  `created_time` bigint(20) NOT NULL DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`question_id`,`created_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `question_version`
-- ----------------------------
DROP TABLE IF EXISTS `question_version`;
CREATE TABLE `question_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `question_id` bigint(20) NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `content` text COLLATE utf8mb4_unicode_ci,
  `html` text COLLATE utf8mb4_unicode_ci,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `version` int(11) DEFAULT '0',
  `is_reviewed` tinyint(1) DEFAULT '0' COMMENT '0 未审核 1 已审核',
  `is_disabled` tinyint(1) DEFAULT '0' COMMENT '0 未禁用，1 禁用',
  `is_published` tinyint(1) DEFAULT '0' COMMENT '0.未发布 1.发布',
  `save_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '备注',
  `reward_type` int(1) DEFAULT '0' COMMENT '0 无悬赏 1积分 2现金',
  `reward_value` decimal(10,2) DEFAULT '0.00' COMMENT '悬赏金额、积分',
  `reward_add` decimal(10,2) DEFAULT '0.00',
  `created_time` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT NULL,
  `updated_time` bigint(20) DEFAULT '0',
  `updated_user_id` bigint(20) DEFAULT '0',
  `updated_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `question_id` (`question_id`,`version`),
  KEY `created_user_id` (`created_user_id`),
  KEY `question_id_2` (`question_id`)
) ENGINE=InnoDB AUTO_INCREMENT=702 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `question_version_tag`
-- ----------------------------
DROP TABLE IF EXISTS `question_version_tag`;
CREATE TABLE `question_version_tag` (
  `version` bigint(20) NOT NULL,
  `tag` bigint(20) NOT NULL,
  PRIMARY KEY (`version`,`tag`),
  UNIQUE KEY `question` (`version`,`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `report_info`
-- ----------------------------
DROP TABLE IF EXISTS `report_info`;
CREATE TABLE `report_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action` int(2) DEFAULT '0',
  `type` int(2) DEFAULT '0',
  `state` int(1) DEFAULT '0',
  `description` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
  `target_id` bigint(20) DEFAULT '0',
  `target_user_id` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) DEFAULT '0',
  `created_time` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `created_user_id` (`created_user_id`),
  KEY `target_id` (`target_id`,`action`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `reward_config`
-- ----------------------------
DROP TABLE IF EXISTS `reward_config`;
CREATE TABLE `reward_config` (
  `id` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` int(1) NOT NULL DEFAULT '0',
  `min` decimal(10,2) DEFAULT '0.00',
  `max` decimal(10,2) DEFAULT '0.00',
  `unit` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  PRIMARY KEY (`id`,`type`),
  UNIQUE KEY `key` (`id`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Records of `reward_config`
-- ----------------------------
BEGIN;
INSERT INTO `reward_config` VALUES ('default', '1', '5.00', '200.00', '积分'), ('default', '2', '1.00', '200.00', '元');
COMMIT;

-- ----------------------------
--  Table structure for `sys_path`
-- ----------------------------
DROP TABLE IF EXISTS `sys_path`;
CREATE TABLE `sys_path` (
  `path` varchar(191) NOT NULL,
  `type` int(11) NOT NULL DEFAULT '0',
  `id` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`path`),
  KEY `type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Records of `sys_path`
-- ----------------------------
BEGIN;
INSERT INTO `sys_path` VALUES ('answer', '0', '0'), ('article', '0', '0'), ('column', '0', '0'), ('dashboard', '0', '0'), ('java', '0', '0'), ('notifications', '0', '0'), ('php', '0', '0'), ('question', '0', '0'), ('search', '0', '0'), ('settings', '0', '0'), ('tag', '0', '0'), ('user', '0', '0'), ('work', '0', '0');
COMMIT;

-- ----------------------------
--  Table structure for `tag`
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `group_id` bigint(20) DEFAULT NULL,
  `star_count` int(11) DEFAULT '0',
  `article_count` int(11) DEFAULT NULL,
  `question_count` int(11) DEFAULT NULL,
  `version` int(11) DEFAULT '0',
  `draft` int(11) DEFAULT '0',
  `is_published` tinyint(1) DEFAULT '0' COMMENT '1已发布 0未发布',
  `is_disabled` tinyint(1) DEFAULT '0' COMMENT '0正常，1禁用',
  `created_time` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT NULL,
  `updated_time` bigint(20) DEFAULT '0',
  `updated_user_id` bigint(20) DEFAULT '0',
  `updated_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tag_name` (`name`),
  KEY `created_user_id` (`created_user_id`),
  KEY `group_id` (`group_id`),
  KEY `is_published` (`is_published`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tag_group`
-- ----------------------------
DROP TABLE IF EXISTS `tag_group`;
CREATE TABLE `tag_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `tag_count` int(11) DEFAULT '0',
  `created_time` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Records of `tag_group`
-- ----------------------------
BEGIN;
INSERT INTO `tag_group` VALUES ('1', '开发语言', '0', '0', '0', '10009'), ('2', '前端开发', '0', '0', '0', '10009'), ('3', 'JavaScript 开发', '0', '0', '0', '10009');
COMMIT;

-- ----------------------------
--  Table structure for `tag_star`
-- ----------------------------
DROP TABLE IF EXISTS `tag_star`;
CREATE TABLE `tag_star` (
  `tag_id` bigint(11) NOT NULL,
  `created_time` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) NOT NULL DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  PRIMARY KEY (`tag_id`,`created_user_id`),
  UNIQUE KEY `user_id` (`tag_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `tag_version`
-- ----------------------------
DROP TABLE IF EXISTS `tag_version`;
CREATE TABLE `tag_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tag_id` bigint(20) DEFAULT NULL,
  `version` int(11) DEFAULT '0',
  `is_reviewed` tinyint(1) DEFAULT '0' COMMENT '0 未审核 1 已审核',
  `is_disabled` tinyint(1) DEFAULT '0' COMMENT '0 未禁用，1 禁用',
  `is_published` tinyint(1) DEFAULT '0' COMMENT '0.未发布 1.发布',
  `icon` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `content` text COLLATE utf8mb4_unicode_ci,
  `html` text COLLATE utf8mb4_unicode_ci,
  `description` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `save_type` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_time` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT NULL,
  `created_user_id` bigint(11) DEFAULT NULL,
  `updated_time` bigint(20) DEFAULT '0',
  `updated_ip` bigint(20) DEFAULT NULL,
  `updated_user_id` bigint(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `tag_id_2` (`tag_id`,`version`),
  KEY `tag_id` (`tag_id`) USING BTREE,
  KEY `created_user_id` (`created_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `upload_config`
-- ----------------------------
DROP TABLE IF EXISTS `upload_config`;
CREATE TABLE `upload_config` (
  `type` varchar(20) NOT NULL DEFAULT '',
  `bucket` varchar(120) DEFAULT '',
  `domain` varchar(120) DEFAULT '',
  `endpoint` varchar(200) DEFAULT '',
  `size` bigint(20) DEFAULT '0',
  PRIMARY KEY (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Records of `upload_config`
-- ----------------------------
BEGIN;
INSERT INTO `upload_config` VALUES ('doc', 'itellyou-file', 'http://cdn-file.itellyou.com', 'http://oss-cn-hangzhou.aliyuncs.com', '10240'), ('file', 'itellyou-file', 'http://cdn-file.itellyou.com', 'http://oss-cn-hangzhou.aliyuncs.com', '10240'), ('image', 'itellyou-image', 'http://cdn-image.itellyou.com', 'http://oss-cn-hangzhou.aliyuncs.com', '5120'), ('video', '', 'http://cdn-video.itellyou.com', 'http://oss-cn-hangzhou.aliyuncs.com', '204800');
COMMIT;

-- ----------------------------
--  Table structure for `upload_file`
-- ----------------------------
DROP TABLE IF EXISTS `upload_file`;
CREATE TABLE `upload_file` (
  `key` varchar(191) NOT NULL,
  `bucket` varchar(191) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `extname` varchar(255) DEFAULT NULL,
  `domain` varchar(255) DEFAULT NULL,
  `source` int(10) DEFAULT '0',
  `size` bigint(20) DEFAULT NULL,
  `created_user_id` bigint(20) DEFAULT '0',
  `created_time` bigint(20) DEFAULT NULL,
  `created_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`key`,`bucket`),
  KEY `extname` (`extname`(191)),
  KEY `created_user_id` (`created_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `upload_file_config`
-- ----------------------------
DROP TABLE IF EXISTS `upload_file_config`;
CREATE TABLE `upload_file_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(120) DEFAULT '',
  `is_image` tinyint(1) DEFAULT '0',
  `is_video` tinyint(1) DEFAULT '0',
  `is_file` tinyint(1) DEFAULT '1',
  `is_doc` tinyint(1) DEFAULT '0',
  `created_user_id` bigint(20) DEFAULT '0',
  `created_time` bigint(20) DEFAULT NULL,
  `created_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Records of `upload_file_config`
-- ----------------------------
BEGIN;
INSERT INTO `upload_file_config` VALUES ('1', 'png', '1', '0', '1', '0', '10009', '0', '0'), ('2', 'jpeg', '1', '0', '1', '0', '10009', '0', '0'), ('3', 'gif', '1', '0', '1', '0', '10009', '0', '0'), ('4', 'jpg', '1', '0', '1', '0', '10009', '0', '0'), ('5', 'svg', '1', '0', '1', '0', '10009', '0', '0'), ('6', 'pdf', '0', '0', '1', '1', '10009', '0', '0'), ('7', 'doc', '0', '0', '1', '1', '10009', '0', '0'), ('8', 'xml', '0', '0', '1', '1', '10009', '0', '0'), ('9', 'mp4', '0', '1', '1', '0', '10009', '0', '0'), ('10', 'docx', '0', '0', '1', '1', '10012', '0', '0'), ('11', 'ppt', '0', '0', '1', '1', '10012', '0', '0'), ('12', 'pptx', '0', '0', '1', '1', '10012', '0', '0'), ('13', 'xls', '0', '0', '1', '1', '10012', '0', '0'), ('14', 'xlsm', '0', '0', '1', '1', '10012', '0', '0'), ('15', 'dot', '0', '0', '1', '1', '10012', '0', '0'), ('16', 'dotx', '0', '0', '1', '1', '10012', '0', '0'), ('17', 'xlsx', '0', '0', '1', '1', '10012', '0', '0'), ('18', 'pot', '0', '0', '1', '1', '10012', '0', '0'), ('19', 'potx', '0', '0', '1', '1', '10012', '0', '0'), ('20', 'xlt', '0', '0', '1', '1', '10012', '0', '0'), ('21', 'xltx', '0', '0', '1', '1', '10012', '0', '0'), ('22', 'pps', '0', '0', '1', '1', '10012', '0', '0'), ('23', 'ppsx', '0', '0', '1', '1', '10012', '0', '0'), ('24', 'csv', '0', '0', '1', '1', '10012', '0', '0'), ('25', 'rtf', '0', '0', '1', '1', '10012', '0', '0'), ('27', 'dps', '0', '0', '1', '1', '10012', '0', '0'), ('28', 'dpt', '0', '0', '1', '1', '10012', '0', '0'), ('29', 'pptm', '0', '0', '1', '1', '10012', '0', '0'), ('30', 'potm', '0', '0', '1', '1', '10012', '0', '0'), ('31', 'ppsm', '0', '0', '1', '1', '10012', '0', '0'), ('32', 'et', '0', '0', '1', '1', '10012', '0', '0'), ('33', 'ett', '0', '0', '1', '1', '10012', '0', '0'), ('35', 'xlsb', '0', '0', '1', '1', '10012', '0', '0'), ('37', 'xltm', '0', '0', '1', '1', '10012', '0', '0'), ('38', 'wps', '0', '0', '1', '1', '10012', '0', '0'), ('39', 'wpt', '0', '0', '1', '1', '10012', '0', '0'), ('42', 'docm', '0', '0', '1', '1', '10012', '0', '0'), ('43', 'dotm', '0', '0', '1', '1', '10012', '0', '0'), ('45', 'lrc', '0', '0', '1', '1', '10012', '0', '0'), ('46', 'c', '0', '0', '1', '1', '10012', '0', '0'), ('47', 'cpp', '0', '0', '1', '1', '10012', '0', '0'), ('48', 'h', '0', '0', '1', '1', '10012', '0', '0'), ('49', 'asm', '0', '0', '1', '1', '10012', '0', '0'), ('50', 's', '0', '0', '1', '1', '10012', '0', '0'), ('51', 'java', '0', '0', '1', '1', '10012', '0', '0'), ('52', 'asp', '0', '0', '1', '1', '10012', '0', '0'), ('53', 'bat', '0', '0', '1', '1', '10012', '0', '0'), ('54', 'bas', '0', '0', '1', '1', '10012', '0', '0'), ('55', 'prg', '0', '0', '1', '1', '10012', '0', '0'), ('56', 'cmd', '0', '0', '1', '1', '10012', '0', '0'), ('58', 'txt', '0', '0', '1', '1', '10012', '0', '0'), ('59', 'log', '0', '0', '1', '1', '10012', '0', '0'), ('61', 'htm', '0', '0', '1', '1', '10012', '0', '0'), ('62', 'html', '0', '0', '1', '1', '10012', '0', '0'), ('68', 'ai', '0', '0', '1', '0', '10012', '0', '0');
COMMIT;

-- ----------------------------
--  Table structure for `user_bank`
-- ----------------------------
DROP TABLE IF EXISTS `user_bank`;
CREATE TABLE `user_bank` (
  `user_id` bigint(11) NOT NULL,
  `cash` decimal(10,2) unsigned NOT NULL DEFAULT '0.00',
  `credit` int(11) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `user_bank_log`
-- ----------------------------
DROP TABLE IF EXISTS `user_bank_log`;
CREATE TABLE `user_bank_log` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `amount` decimal(10,2) DEFAULT NULL,
  `type` tinyint(1) DEFAULT '0' COMMENT '1.积分，2.金钱',
  `balance` decimal(10,2) DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
  `data_type` int(10) DEFAULT '0' COMMENT '1.提问悬赏',
  `data_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
  `created_time` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `data_type` (`data_type`,`data_key`(191)),
  KEY `created_user_id` (`created_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `user_draft`
-- ----------------------------
DROP TABLE IF EXISTS `user_draft`;
CREATE TABLE `user_draft` (
  `data_type` int(2) NOT NULL,
  `data_key` varchar(30) NOT NULL,
  `url` varchar(500) DEFAULT '',
  `title` varchar(255) DEFAULT '',
  `content` text,
  `author_id` bigint(20) DEFAULT '0',
  `created_time` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT NULL,
  `created_user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`data_type`,`data_key`,`created_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `user_info`
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `login_name` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `login_password` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '登录密码',
  `pay_password` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '支付密码',
  `name` varchar(180) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `gender` int(1) DEFAULT '0' COMMENT '0.保密 1.男性 2.女性',
  `birthday` bigint(20) DEFAULT '0',
  `mobile` varchar(20) CHARACTER SET utf8 DEFAULT '',
  `mobile_status` tinyint(1) DEFAULT '0',
  `email` varchar(120) CHARACTER SET utf8 DEFAULT '',
  `email_status` tinyint(1) DEFAULT '0',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `introduction` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `profession` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `is_disabled` tinyint(1) DEFAULT '0',
  `star_count` int(11) DEFAULT '0',
  `follower_count` int(11) DEFAULT '0',
  `question_count` int(11) DEFAULT '0',
  `answer_count` int(11) DEFAULT '0',
  `article_count` int(11) DEFAULT '0',
  `column_count` int(11) DEFAULT '0',
  `collection_count` int(11) DEFAULT '0',
  `created_time` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  `updated_time` bigint(20) DEFAULT '0',
  `updated_user_id` bigint(20) DEFAULT '0',
  `updated_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name` (`login_name`) USING BTREE,
  UNIQUE KEY `mobile_number` (`mobile`) USING BTREE,
  UNIQUE KEY `email_address` (`email`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10016 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `user_login_log`
-- ----------------------------
DROP TABLE IF EXISTS `user_login_log`;
CREATE TABLE `user_login_log` (
  `token` varchar(191) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_disabled` tinyint(1) DEFAULT '0',
  `client_type` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'unknown',
  `created_user_id` bigint(20) NOT NULL DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  `created_time` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`token`),
  UNIQUE KEY `token` (`token`) USING BTREE,
  KEY `created_user_id` (`created_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
--  Table structure for `user_notification`
-- ----------------------------
DROP TABLE IF EXISTS `user_notification`;
CREATE TABLE `user_notification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `is_read` tinyint(1) DEFAULT '0',
  `is_deleted` tinyint(1) DEFAULT '0',
  `receive_id` bigint(20) DEFAULT '0',
  `type` int(2) DEFAULT '0',
  `action` int(2) DEFAULT '0',
  `target_id` bigint(20) DEFAULT '0',
  `merge_count` int(11) DEFAULT '1',
  `created_time` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  `updated_time` bigint(20) DEFAULT '0',
  `updated_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `type` (`type`,`action`),
  KEY `receive_id` (`receive_id`),
  KEY `target_id` (`target_id`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `user_notification_actors`
-- ----------------------------
DROP TABLE IF EXISTS `user_notification_actors`;
CREATE TABLE `user_notification_actors` (
  `notification_id` bigint(20) NOT NULL DEFAULT '0',
  `user_id` bigint(20) NOT NULL DEFAULT '0',
  `target_id` bigint(20) DEFAULT '0',
  PRIMARY KEY (`notification_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `user_notification_display`
-- ----------------------------
DROP TABLE IF EXISTS `user_notification_display`;
CREATE TABLE `user_notification_display` (
  `user_id` bigint(20) NOT NULL,
  `action` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `value` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`,`action`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `user_notification_display_default`
-- ----------------------------
DROP TABLE IF EXISTS `user_notification_display_default`;
CREATE TABLE `user_notification_display_default` (
  `action` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `value` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`action`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Records of `user_notification_display_default`
-- ----------------------------
BEGIN;
INSERT INTO `user_notification_display_default` VALUES ('1', '1', '1'), ('1', '3', '1'), ('1', '4', '1'), ('1', '5', '1'), ('2', '3', '1'), ('2', '4', '1'), ('2', '6', '1'), ('2', '7', '1'), ('2', '8', '1'), ('3', '2', '1'), ('3', '3', '1'), ('3', '4', '1'), ('3', '6', '1'), ('3', '7', '1'), ('3', '8', '1'), ('4', '3', '1'), ('4', '4', '1');
COMMIT;

-- ----------------------------
--  Table structure for `user_notification_mark`
-- ----------------------------
DROP TABLE IF EXISTS `user_notification_mark`;
CREATE TABLE `user_notification_mark` (
  `user_id` bigint(20) NOT NULL,
  `updated_time` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `user_operational`
-- ----------------------------
DROP TABLE IF EXISTS `user_operational`;
CREATE TABLE `user_operational` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` int(2) DEFAULT '0',
  `action` int(2) DEFAULT '0',
  `target_id` bigint(20) DEFAULT '0',
  `target_user_id` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) DEFAULT '0',
  `created_time` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `type` (`type`,`action`),
  KEY `target_user_id` (`target_user_id`),
  KEY `created_user_id` (`created_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=126 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `user_star`
-- ----------------------------
DROP TABLE IF EXISTS `user_star`;
CREATE TABLE `user_star` (
  `user_id` bigint(20) NOT NULL,
  `created_time` bigint(20) NOT NULL DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`created_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `user_verify`
-- ----------------------------
DROP TABLE IF EXISTS `user_verify`;
CREATE TABLE `user_verify` (
  `key` varchar(191) NOT NULL,
  `is_disabled` tinyint(1) DEFAULT '0',
  `created_time` bigint(20) DEFAULT '0',
  `created_user_id` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  PRIMARY KEY (`key`),
  KEY `created_user_id` (`created_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `view_info`
-- ----------------------------
DROP TABLE IF EXISTS `view_info`;
CREATE TABLE `view_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT '',
  `data_type` int(11) DEFAULT NULL,
  `data_key` varchar(255) DEFAULT NULL,
  `os` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
  `browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
  `created_user_id` bigint(11) DEFAULT '0',
  `created_time` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  `updated_time` bigint(20) DEFAULT '0',
  `updated_ip` bigint(20) DEFAULT '0',
  `updated_user_id` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `data_type` (`data_type`,`data_key`,`created_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
