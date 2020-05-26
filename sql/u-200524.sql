CREATE TABLE `sys_setting` (
                               `key` varchar(191) NOT NULL DEFAULT 'default',
                               `name` varchar(255) NOT NULL DEFAULT 'ITELLYOU',
                               `logo` varchar(255) DEFAULT '',
                               `icp_text` varchar(255) DEFAULT '',
                               `copyright` varchar(255) DEFAULT '',
                               `company_name` varchar(255) DEFAULT '',
                               `user_agreement_link` varchar(255) DEFAULT '',
                               `footer_scripts` text DEFAULT '',
                               PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE `sys_link` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `text` varchar(200) NOT NULL DEFAULT '',
                            `link` varchar(200) NOT NULL DEFAULT '',
                            `target` varchar(255) DEFAULT '_blank',
                            `created_user_id` bigint(20) DEFAULT '0',
                            `created_time` bigint(20) DEFAULT '0',
                            `created_ip` bigint(20) DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;