CREATE TABLE `itellyou`.`user_payment` (
	`id` varchar(171) NOT NULL,
	`amount` decimal(10,2) NOT NULL DEFAULT 0.00,
	`type` int(1) NOT NULL DEFAULT 0,
	`subject` varchar(255) NOT NULL DEFAULT '',
	`status` int(1) NOT NULL DEFAULT 0,
	`created_user_id` bigint NOT NULL DEFAULT 0,
	`created_ip` bigint NOT NULL DEFAULT 0,
	`created_time` bigint NOT NULL DEFAULT 0,
	`updated_user_id` bigint NOT NULL DEFAULT 0,
	`updated_ip` bigint NOT NULL DEFAULT 0,
	`updated_time` bigint NOT NULL DEFAULT 0,
	PRIMARY KEY (`id`)
) COMMENT='';
ALTER TABLE `itellyou`.`user_payment` ADD INDEX  (`status`) comment '', ADD INDEX  (`created_user_id`) comment '';
ALTER TABLE `itellyou`.`alipay_config` CHANGE COLUMN `alipay_key` `alipay_key` varchar(2000) DEFAULT '';
DROP TABLE IF EXISTS WORKER_NODE;
CREATE TABLE WORKER_NODE
(
ID BIGINT NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',
HOST_NAME VARCHAR(64) NOT NULL COMMENT 'host name',
PORT VARCHAR(64) NOT NULL COMMENT 'port',
TYPE INT NOT NULL COMMENT 'node type: ACTUAL or CONTAINER',
LAUNCH_DATE DATE NOT NULL COMMENT 'launch date',
MODIFIED TIMESTAMP NOT NULL COMMENT 'modified time',
CREATED TIMESTAMP NOT NULL COMMENT 'created time',
PRIMARY KEY(ID)
)
 COMMENT='DB WorkerID Assigner for UID Generator',ENGINE = INNODB;

 ALTER TABLE `itellyou`.`user_bank_log` CHANGE COLUMN `type` `type` int(1) DEFAULT 0 COMMENT '1.积分，2.金钱';