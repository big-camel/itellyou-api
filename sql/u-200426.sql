CREATE TABLE `user_third_account` (
  `user_id` bigint(20) NOT NULL,
  `type` int(1) NOT NULL,
  `key` varchar(171) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `created_time` bigint(20) DEFAULT NULL,
  `created_ip` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
ALTER TABLE `itellyou`.`alipay_config` ADD COLUMN `redirect_uri` varchar(255) DEFAULT '' AFTER `return_url`;
CREATE TABLE `user_third_log` (
  `id` varchar(171) NOT NULL,
  `type` int(1) NOT NULL,
  `action` int(1) NOT NULL,
  `is_verify` tinyint(1) NOT NULL DEFAULT '0',
  `redirect_uri` varchar(255) DEFAULT '',
  `created_user_id` bigint(20) DEFAULT '0',
  `created_time` bigint(20) DEFAULT '0',
  `created_ip` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;