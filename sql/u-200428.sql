CREATE TABLE `github_config` (
                                 `id` varchar(191) NOT NULL,
                                 `secret` varchar(255) NOT NULL DEFAULT '',
                                 `gateway` varchar(255) DEFAULT '',
                                 `redirect_uri` varchar(255) DEFAULT '',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
ALTER TABLE `itellyou`.`user_third_account` ADD COLUMN `home` varchar(255) DEFAULT '' AFTER `avatar`, ADD COLUMN `star` bigint DEFAULT 0 AFTER `home`;