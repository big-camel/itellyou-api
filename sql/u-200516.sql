CREATE TABLE `article_paid_read` (
                                     `article_id` bigint(20) NOT NULL DEFAULT '0',
                                     `paid_type` int(1) DEFAULT '0',
                                     `paid_to_read` tinyint(1) DEFAULT '0',
                                     `paid_amount` decimal(10,2) DEFAULT '0.00',
                                     `star_to_read` tinyint(1) DEFAULT '0',
                                     `free_read_scale` decimal(10,2) DEFAULT '0.00',
                                     PRIMARY KEY (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `question_answer_paid_read` (
                                     `answer_id` bigint(20) NOT NULL DEFAULT '0',
                                     `paid_type` int(1) DEFAULT '0',
                                     `paid_to_read` tinyint(1) DEFAULT '0',
                                     `paid_amount` decimal(10,2) DEFAULT '0.00',
                                     `star_to_read` tinyint(1) DEFAULT '0',
                                     `free_read_scale` decimal(10,2) DEFAULT '0.00',
                                     PRIMARY KEY (`answer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;