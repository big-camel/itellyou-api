CREATE TABLE `reward_log` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT,
                              `bank_type` int(1) NOT NULL DEFAULT '0',
                              `data_type` int(11) NOT NULL DEFAULT '0',
                              `data_key` bigint(20) NOT NULL DEFAULT '0',
                              `user_id` bigint(20) NOT NULL DEFAULT '0',
                              `created_user_id` bigint(20) DEFAULT '0',
                              `created_time` bigint(20) DEFAULT '0',
                              `created_ip` bigint(20) DEFAULT '0',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
ALTER TABLE `reward_log` ADD COLUMN `amount` decimal(10,2) UNSIGNED NOT NULL DEFAULT 0.00 AFTER `data_key`;