CREATE TABLE `user_notification_queue` (
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
) ENGINE=InnoDB AUTO_INCREMENT=206 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;