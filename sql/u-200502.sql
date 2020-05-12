CREATE TABLE `user_activity` (
                                 `type` int(2) NOT NULL DEFAULT '0',
                                 `action` int(2) NOT NULL DEFAULT '0',
                                 `target_id` bigint(20) NOT NULL DEFAULT '0',
                                 `target_user_id` bigint(20) DEFAULT '0',
                                 `created_user_id` bigint(20) NOT NULL DEFAULT '0',
                                 `created_time` bigint(20) DEFAULT '0',
                                 `created_ip` bigint(20) DEFAULT '0',
                                 PRIMARY KEY (`type`,`action`,`target_id`,`created_user_id`),
                                 KEY `type` (`type`,`action`),
                                 KEY `target_user_id` (`target_user_id`),
                                 KEY `created_user_id` (`created_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;
ALTER TABLE `user_bank` ADD COLUMN `score` int DEFAULT 0 AFTER `credit`;
ALTER TABLE `user_bank_log` ADD COLUMN `action` int(2) DEFAULT 0  AFTER `amount`, CHANGE COLUMN `type` `type` int(2) DEFAULT 0 COMMENT '1.积分，2.金钱，3.分数';
ALTER TABLE `user_bank_config` ADD COLUMN `only_once` tinyint DEFAULT 0 AFTER `creater_remark`;