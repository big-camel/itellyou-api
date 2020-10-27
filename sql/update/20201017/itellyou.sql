ALTER TABLE `itellyou`.`article_comment`
CHANGE COLUMN `support` `support_count` int(0) NULL DEFAULT 0 AFTER `comment_count`,
CHANGE COLUMN `oppose` `oppose_count` int(0) NULL DEFAULT 0 AFTER `support_count`;

ALTER TABLE `itellyou`.`article_info`
    CHANGE COLUMN `view` `view_count` int(0) NULL DEFAULT 0 AFTER `comment_count`,
    CHANGE COLUMN `support` `support_count` int(0) NULL DEFAULT 0 AFTER `view_count`,
    CHANGE COLUMN `oppose` `oppose_count` int(0) NULL DEFAULT 0 AFTER `support_count`;

ALTER TABLE `itellyou`.`question_answer`
    CHANGE COLUMN `comments` `comment_count` int(0) NULL DEFAULT 0 AFTER `is_adopted`,
    CHANGE COLUMN `support` `support_count` int(0) NULL DEFAULT 0 AFTER `comment_count`,
    CHANGE COLUMN `oppose` `oppose_count` int(0) NULL DEFAULT 0 AFTER `support_count`,
    CHANGE COLUMN `view` `view_count` int(0) NULL DEFAULT 0 AFTER `oppose_count`;

ALTER TABLE `itellyou`.`question_answer_comment`
    CHANGE COLUMN `comments` `comment_count` int(0) NULL DEFAULT 0 AFTER `html`,
    CHANGE COLUMN `support` `support_count` int(0) NULL DEFAULT 0 AFTER `comment_count`,
    CHANGE COLUMN `oppose` `oppose_count` int(0) NULL DEFAULT 0 AFTER `support_count`;

ALTER TABLE `itellyou`.`question_comment`
    CHANGE COLUMN `comments` `comment_count` int(0) NULL DEFAULT 0 AFTER `html`,
    CHANGE COLUMN `support` `support_count` int(0) NULL DEFAULT 0 AFTER `comment_count`,
    CHANGE COLUMN `oppose` `oppose_count` int(0) NULL DEFAULT 0 AFTER `support_count`;

ALTER TABLE `itellyou`.`question_info`
    CHANGE COLUMN `answers` `answer_count` int(0) NULL DEFAULT 0 AFTER `adoption_id`,
    CHANGE COLUMN `comments` `comment_count` int(0) NULL DEFAULT 0 AFTER `answer_count`,
    CHANGE COLUMN `view` `view_count` int(0) NOT NULL DEFAULT 0 AFTER `comment_count`,
    CHANGE COLUMN `support` `support_count` int(0) NULL DEFAULT 0 AFTER `cover`,
    CHANGE COLUMN `oppose` `oppose_count` int(0) NULL DEFAULT 0 AFTER `support_count`,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`id`) USING BTREE;

ALTER TABLE `itellyou`.`software_comment`
    CHANGE COLUMN `support` `support_count` int(0) NULL DEFAULT 0 AFTER `comment_count`,
    CHANGE COLUMN `oppose` `oppose_count` int(0) NULL DEFAULT 0 AFTER `support_count`;

ALTER TABLE `itellyou`.`software_info`
    CHANGE COLUMN `view` `view_count` int(0) NULL DEFAULT 0 AFTER `comment_count`,
    CHANGE COLUMN `support` `support_count` int(0) NULL DEFAULT 0 AFTER `view_count`,
    CHANGE COLUMN `oppose` `oppose_count` int(0) NULL DEFAULT 0 AFTER `support_count`;

-- ----------------------------
-- Table structure for area_city
-- ----------------------------
DROP TABLE IF EXISTS `area_city`;
CREATE TABLE `area_city` (
                             `code` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                             `province_code` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                             `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                             `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                             `pinyin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                             `pinyin_short` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                             PRIMARY KEY (`code`),
                             KEY `province_code` (`province_code`),
                             KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for area_country
-- ----------------------------
DROP TABLE IF EXISTS `area_country`;
CREATE TABLE `area_country` (
                                `code` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                                `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                                `area_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                                `iso_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                                `pinyin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                                `pinyin_short` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                                PRIMARY KEY (`code`),
                                KEY `area_code` (`area_code`),
                                KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for area_county
-- ----------------------------
DROP TABLE IF EXISTS `area_county`;
CREATE TABLE `area_county` (
                               `code` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                               `city_code` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                               `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                               `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                               `pinyin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                               `pinyin_short` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                               PRIMARY KEY (`code`),
                               KEY `city_code` (`city_code`),
                               KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for area_province
-- ----------------------------
DROP TABLE IF EXISTS `area_province`;
CREATE TABLE `area_province` (
                                 `code` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `country_code` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                                 `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                                 `pinyin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                                 `pinyin_short` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                                 PRIMARY KEY (`code`),
                                 KEY `country_code` (`country_code`),
                                 KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for area_town
-- ----------------------------
DROP TABLE IF EXISTS `area_town`;
CREATE TABLE `area_town` (
                             `code` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                             `county_code` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                             `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
                             `alias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                             `pinyin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                             `pinyin_short` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                             PRIMARY KEY (`code`),
                             KEY `county_code` (`county_code`),
                             KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for statistics_income
-- ----------------------------
DROP TABLE IF EXISTS `statistics_income`;
CREATE TABLE `statistics_income` (
                                     `id` bigint NOT NULL AUTO_INCREMENT,
                                     `user_id` bigint DEFAULT '0',
                                     `date` bigint DEFAULT '0',
                                     `total_amount` decimal(10,2) DEFAULT NULL COMMENT '当日总计收益',
                                     `tip_amount` decimal(10,2) DEFAULT '0.00' COMMENT '打赏收到的金额',
                                     `reward_amount` decimal(10,2) DEFAULT '0.00' COMMENT '回答被采纳收到的奖赏',
                                     `sharing_amount` decimal(10,2) DEFAULT '0.00' COMMENT '平台利润分成金额',
                                     `sell_amount` decimal(10,2) DEFAULT '0.00' COMMENT '商品售卖金额',
                                     `other_amount` decimal(10,2) DEFAULT NULL COMMENT '其它收益',
                                     `created_time` bigint DEFAULT '0',
                                     `created_user_id` bigint DEFAULT '0',
                                     `created_ip` bigint DEFAULT '0',
                                     `updated_time` bigint DEFAULT '0',
                                     `updated_ip` bigint DEFAULT '0',
                                     `updated_user_id` bigint DEFAULT '0',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `user_id_2` (`user_id`,`date`),
                                     KEY `user_id` (`user_id`),
                                     KEY `created_user_id` (`created_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for statistics_info
-- ----------------------------
DROP TABLE IF EXISTS `statistics_info`;
CREATE TABLE `statistics_info` (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `user_id` bigint DEFAULT '0',
                                   `date` bigint DEFAULT '0',
                                   `data_type` int DEFAULT NULL,
                                   `data_key` bigint DEFAULT '0',
                                   `view_count` int DEFAULT '0',
                                   `comment_count` int DEFAULT '0',
                                   `support_count` int DEFAULT '0',
                                   `oppose_count` int DEFAULT '0',
                                   `star_count` int DEFAULT '0',
                                   `created_time` bigint DEFAULT '0',
                                   `created_user_id` bigint DEFAULT '0',
                                   `created_ip` bigint DEFAULT '0',
                                   `updated_time` bigint DEFAULT '0',
                                   `updated_ip` bigint DEFAULT '0',
                                   `updated_user_id` bigint DEFAULT '0',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE KEY `type_key_date` (`data_type`,`date`,`data_key`) USING BTREE,
                                   KEY `data_type` (`data_type`),
                                   KEY `created_user_id` (`created_user_id`) USING BTREE,
                                   KEY `data_key` (`data_key`),
                                   KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for sys_ad
-- ----------------------------
DROP TABLE IF EXISTS `sys_ad`;
CREATE TABLE `sys_ad` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `type` int DEFAULT '0',
                          `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
                          `data_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
                          `enabled_foreign` tinyint(1) DEFAULT '0',
                          `enabled_cn` tinyint(1) DEFAULT '1',
                          `created_time` bigint DEFAULT '0',
                          `created_user_id` bigint DEFAULT '0',
                          `created_ip` bigint DEFAULT '0',
                          `updated_time` bigint DEFAULT '0',
                          `updated_ip` bigint DEFAULT '0',
                          `updated_user_id` bigint DEFAULT '0',
                          PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for sys_ad_slot
-- ----------------------------
DROP TABLE IF EXISTS `sys_ad_slot`;
CREATE TABLE `sys_ad_slot` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `ad_id` bigint DEFAULT '0',
                               `slot_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
                               `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
                               `width` int DEFAULT '200',
                               `height` int DEFAULT '200',
                               `style` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT '',
                               `format` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT 'auto',
                               `created_time` bigint DEFAULT '0',
                               `created_user_id` bigint DEFAULT '0',
                               `created_ip` bigint DEFAULT '0',
                               `updated_time` bigint DEFAULT '0',
                               `updated_ip` bigint DEFAULT '0',
                               `updated_user_id` bigint DEFAULT '0',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
-- ----------------------------
-- Table structure for sys_income
-- ----------------------------
DROP TABLE IF EXISTS `sys_income`;
CREATE TABLE `sys_income` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `date` bigint DEFAULT '0' COMMENT '收益日期时间戳，唯一',
                              `amount` decimal(10,2) DEFAULT '0.00' COMMENT '收益金额',
                              `created_time` bigint DEFAULT '0',
                              `created_user_id` bigint DEFAULT '0',
                              `created_ip` bigint DEFAULT '0',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `date` (`date`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for sys_income_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_income_config`;
CREATE TABLE `sys_income_config` (
                                     `id` bigint NOT NULL AUTO_INCREMENT,
                                     `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '配置名称',
                                     `scale` decimal(10,2) DEFAULT '0.00' COMMENT '分成比例',
                                     `is_deleted` tinyint(1) DEFAULT '0',
                                     `remark` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
                                     `created_time` bigint DEFAULT '0',
                                     `created_user_id` bigint DEFAULT '0',
                                     `created_ip` bigint DEFAULT '0',
                                     `updated_time` bigint DEFAULT '0',
                                     `updated_ip` bigint DEFAULT '0',
                                     `updated_user_id` bigint DEFAULT '0',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for sys_income_related
-- ----------------------------
DROP TABLE IF EXISTS `sys_income_related`;
CREATE TABLE `sys_income_related` (
                                      `id` bigint NOT NULL AUTO_INCREMENT,
                                      `income_id` bigint NOT NULL,
                                      `config_id` int NOT NULL,
                                      `amount` decimal(10,2) DEFAULT NULL,
                                      `created_time` bigint DEFAULT '0',
                                      `created_user_id` bigint DEFAULT '0',
                                      `created_ip` bigint DEFAULT '0',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      UNIQUE KEY `income_id_2` (`income_id`,`config_id`),
                                      KEY `income_id` (`income_id`),
                                      KEY `config_id` (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for sys_income_tip_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_income_tip_config`;
CREATE TABLE `sys_income_tip_config` (
                                         `id` bigint NOT NULL AUTO_INCREMENT,
                                         `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '配置名称',
                                         `data_type` int DEFAULT '0' COMMENT '实体类型',
                                         `min_view` int DEFAULT '0' COMMENT '需要分成最小阅读量',
                                         `min_comment` int DEFAULT '0' COMMENT '需要分成最小评论数',
                                         `min_support` int DEFAULT '0' COMMENT '需要分成最小点赞数',
                                         `min_oppose` int DEFAULT '0' COMMENT '需要分成最小反对数',
                                         `min_star` int DEFAULT '0' COMMENT '需要分成最小收藏数',
                                         `view_weight` decimal(10,2) DEFAULT '0.05' COMMENT '阅读数所占权重',
                                         `comment_weight` decimal(10,2) DEFAULT '0.35' COMMENT '评论数所占权重',
                                         `support_weight` decimal(10,2) DEFAULT '0.15' COMMENT '点赞数所占权重',
                                         `oppose_weight` decimal(10,2) DEFAULT '-0.15' COMMENT '反对数所占权重',
                                         `star_weight` decimal(10,2) DEFAULT '0.45' COMMENT '收藏数所占权重',
                                         `min_amount` decimal(10,2) DEFAULT '0.00' COMMENT '最小派送金额',
                                         `max_amount` decimal(10,2) DEFAULT '0.00' COMMENT '最大派送金额',
                                         `max_user_count` int DEFAULT '0' COMMENT '最大派送用户数',
                                         `created_time` bigint DEFAULT '0',
                                         `created_user_id` bigint DEFAULT '0',
                                         `created_ip` bigint DEFAULT '0',
                                         `updated_time` bigint DEFAULT '0',
                                         `updated_ip` bigint DEFAULT '0',
                                         `updated_user_id` bigint DEFAULT '0',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
