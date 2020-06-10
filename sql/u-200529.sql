ALTER TABLE `itellyou`.`article_info` ADD COLUMN `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' AFTER `id`, ADD COLUMN `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' AFTER `title`, CHANGE COLUMN `custom_description` `custom_description` varchar(500) DEFAULT '' AFTER `description`, ADD COLUMN `column_id` bigint(20) DEFAULT NULL AFTER `custom_description`, ADD COLUMN `source_type` int(2) DEFAULT 1 AFTER `column_id`, ADD COLUMN `source_data` varchar(500) DEFAULT '' AFTER `source_type`, CHANGE COLUMN `cover` `cover` varchar(255) DEFAULT '' AFTER `source_data`, CHANGE COLUMN `is_published` `is_published` tinyint(1) DEFAULT 0 COMMENT '1已发布 0未发布' AFTER `cover`, CHANGE COLUMN `is_deleted` `is_deleted` tinyint(1) DEFAULT 0 COMMENT '是否删除' AFTER `is_published`, CHANGE COLUMN `is_disabled` `is_disabled` tinyint(1) DEFAULT 1 COMMENT '1正常，2禁用' AFTER `is_deleted`, CHANGE COLUMN `version` `version` int(11) DEFAULT 0 COMMENT '已发布版本' AFTER `is_disabled`, CHANGE COLUMN `draft` `draft` int(11) DEFAULT 0 AFTER `version`, CHANGE COLUMN `comment_count` `comment_count` int(11) DEFAULT 0 AFTER `draft`, CHANGE COLUMN `view` `view` int(11) DEFAULT 0 AFTER `comment_count`, CHANGE COLUMN `support` `support` int(11) DEFAULT 0 AFTER `view`, CHANGE COLUMN `oppose` `oppose` int(11) DEFAULT 0 AFTER `support`, CHANGE COLUMN `star_count` `star_count` int(11) DEFAULT 0 AFTER `oppose`;
CREATE TABLE `article_tag` (
                               `article_id` bigint(20) NOT NULL,
                               `tag_id` bigint(20) NOT NULL,
                               PRIMARY KEY (`article_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
ALTER TABLE `itellyou`.`column_star` ADD INDEX  (`column_id`) comment '', ADD INDEX  (`created_user_id`) comment '';
ALTER TABLE `itellyou`.`article_star` ADD INDEX  (`article_id`) comment '', ADD INDEX  (`created_user_id`) comment '';
ALTER TABLE `itellyou`.`question_answer_star` ADD INDEX  (`answer_id`) comment '', ADD INDEX  (`created_user_id`) comment '';
ALTER TABLE `itellyou`.`question_star` ADD INDEX  (`question_id`) comment '', ADD INDEX  (`created_user_id`) comment '';
ALTER TABLE `itellyou`.`question_answer` ADD COLUMN `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' AFTER `question_id`;
CREATE TABLE `question_tag` (
                                `question_id` bigint(20) NOT NULL,
                                `tag_id` bigint(20) NOT NULL,
                                PRIMARY KEY (`question_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
ALTER TABLE `itellyou`.`question_info` ADD COLUMN `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' AFTER `id`, ADD COLUMN `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' AFTER `title`, ADD COLUMN `reward_type` int(1) DEFAULT 0 COMMENT '0 无悬赏 1积分 2现金' AFTER `description`, ADD COLUMN `reward_value` decimal(10,2) DEFAULT 0.00 COMMENT '悬赏金额、积分' AFTER `reward_type`, ADD COLUMN `reward_add` decimal(10,2) DEFAULT 0.00 AFTER `reward_value`;
ALTER TABLE `itellyou`.`tag`
    ADD COLUMN `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' AFTER `name`,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (`id`) USING BTREE;