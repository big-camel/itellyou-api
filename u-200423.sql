ALTER TABLE `itellyou`.`view_info` CHANGE COLUMN `data_key` `data_key` bigint(20) DEFAULT NULL, DROP INDEX `data_type`, ADD INDEX `data_type` USING BTREE (`data_type`, `data_key`, `created_user_id`) comment '';
ADD COLUMN `is_default` tinyint(1) DEFAULT 0 AFTER `is_enable`;
ALTER TABLE `itellyou`.`alipay_config` ADD COLUMN `alipay_key` varchar(2000) DEFAULT NULL AFTER `public_key`;
ALTER TABLE `itellyou`.`alipay_config` CHANGE COLUMN `private_key` `private_key` varchar(2000) DEFAULT '', CHANGE COLUMN `public_key` `public_key` varchar(2000) DEFAULT '';
ALTER TABLE `itellyou`.`alipay_config` ADD COLUMN `public_cert_path` varchar(255) AFTER `gateway`, ADD COLUMN `alipay_cert_path` varchar(255) AFTER `public_cert_path`, ADD COLUMN `root_cert_path` varchar(255) AFTER `alipay_cert_path`;