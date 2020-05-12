CREATE TABLE `sys_permission` (
                                  `name` varchar(191) NOT NULL DEFAULT '' COMMENT '权限名称',
                                  `type` int(11) DEFAULT '0' COMMENT '权限类型，比如 url，button',
                                  `method` int(11) DEFAULT '0' COMMENT '权限请求/操作方式：post，click ',
                                  `data` varchar(255) DEFAULT '' COMMENT '权限数据，例如：url，',
                                  `login` tinyint(4) DEFAULT '0' COMMENT '是否需要登录',
                                  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
ALTER TABLE `sys_permission` ADD COLUMN `platform` int(2) DEFAULT 0 COMMENT '权限平台，api/web/admin' AFTER `name`, CHANGE COLUMN `type` `type` int(2) DEFAULT 0 COMMENT '权限类型，比如 url，button', CHANGE COLUMN `method` `method` int(2) DEFAULT 0 COMMENT '权限请求/操作方式：post，click ';
ALTER TABLE `sys_permission` ADD COLUMN `remark` varchar(200) DEFAULT '' AFTER `login`;
CREATE TABLE `sys_role` (
                            `id` tinyint(4) NOT NULL AUTO_INCREMENT,
                            `name` varchar(255) NOT NULL DEFAULT '',
                            `description` varchar(200) DEFAULT '',
                            `disabled` tinyint(1) DEFAULT '0',
                            `created_user_id` bigint(20) DEFAULT NULL,
                            `created_time` bigint(20) DEFAULT NULL,
                            `created_ip` bigint(20) DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE `sys_role_permission` (
                                       `role_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '角色编号',
                                       `permission_name` varchar(191) NOT NULL DEFAULT '',
                                       `created_user_id` bigint(20) DEFAULT NULL,
                                       `created_time` bigint(20) DEFAULT NULL,
                                       `created_ip` bigint(20) DEFAULT NULL,
                                       PRIMARY KEY (`role_id`,`permission_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE `user_rank` (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                             `name` varchar(191) DEFAULT NULL,
                             `min_score` int(11) DEFAULT '0' COMMENT '最小权限分',
                             `max_score` int(11) DEFAULT '0' COMMENT '最大权限分',
                             `created_user_id` bigint(20) DEFAULT NULL,
                             `created_time` bigint(20) DEFAULT NULL,
                             `created_ip` bigint(20) DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE `user_rank_role` (
                                  `rank_id` bigint(20) NOT NULL,
                                  `role_id` bigint(20) NOT NULL DEFAULT '0',
                                  `created_user_id` bigint(20) DEFAULT NULL,
                                  `created_time` bigint(20) DEFAULT '0',
                                  `created_ip` bigint(20) DEFAULT NULL,
                                  PRIMARY KEY (`rank_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE `user_role` (
                             `user_id` bigint(20) NOT NULL,
                             `role_id` bigint(20) NOT NULL DEFAULT '0',
                             `created_user_id` bigint(20) DEFAULT NULL,
                             `created_time` bigint(20) DEFAULT NULL,
                             `created_ip` bigint(20) DEFAULT NULL,
                             PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `sys_role` CHANGE COLUMN `name` `name` varchar(191) NOT NULL DEFAULT '', ADD UNIQUE  (`name`, `created_user_id`) comment '';
ALTER TABLE `sys_role` ADD COLUMN `system` tinyint(1) DEFAULT 0 AFTER `disabled`;