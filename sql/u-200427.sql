CREATE TABLE `user_withdraw` (
                                 `id` varchar(171) NOT NULL,
                                 `amount` decimal(10,2) NOT NULL DEFAULT '0.00',
                                 `subject` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '提现',
                                 `commissionCharge` decimal(10,2) NOT NULL DEFAULT '0.00',
                                 `status` int(1) NOT NULL DEFAULT '0',
                                 `created_user_id` bigint(20) NOT NULL DEFAULT '0',
                                 `created_ip` bigint(20) NOT NULL DEFAULT '0',
                                 `created_time` bigint(20) NOT NULL DEFAULT '0',
                                 `updated_user_id` bigint(20) NOT NULL DEFAULT '0',
                                 `updated_ip` bigint(20) NOT NULL DEFAULT '0',
                                 `updated_time` bigint(20) NOT NULL DEFAULT '0',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE `user_withdraw_config` (
                                        `id` varchar(171) NOT NULL,
                                        `min` decimal(10,2) NOT NULL DEFAULT '10.00' COMMENT '单次最低取现额度',
                                        `max` decimal(10,2) NOT NULL DEFAULT '2000.00' COMMENT '单次最大取现额度',
                                        `auto` decimal(10,2) NOT NULL DEFAULT '200.00' COMMENT '支付宝单次自动提现最高额度，也就是需要人工审核的最低额度',
                                        `rate` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '提现手续费率',
                                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
ALTER TABLE `itellyou`.`user_third_account` DROP PRIMARY KEY, ADD PRIMARY KEY (`user_id`, `type`, `key`);