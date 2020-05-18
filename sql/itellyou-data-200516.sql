update `itellyou`.`sys_permission` set `name`='api_answer_version_compare', `platform`='2', `type`='1', `method`='1', `data`='/question/{questionId:\\d+}/answer/{answerId:\\d+}/version/{current:\\d+}...{target:\\d+}', `remark`='对比回答版本详情' where `name`='api_answer_version_compare';
update `itellyou`.`sys_permission` set `name`='api_answer_version_compare_user', `platform`='2', `type`='1', `method`='1', `data`='/question/{questionId:\\d+}/answer/version/{current:\\d+}...{target:\\d+}', `remark`='对比用户唯一回答版本详情' where `name`='api_answer_version_compare_user';
update `itellyou`.`sys_permission` set `name`='api_answer_version_detail', `platform`='2', `type`='1', `method`='1', `data`='/question/{questionId:\\d+}/answer/{answerId:\\d+}/version/{versionId:\\d+}', `remark`='获取回答版本详情' where `name`='api_answer_version_detail';
update `itellyou`.`sys_permission` set `name`='api_answer_version_detail_user', `platform`='2', `type`='1', `method`='1', `data`='/question/{questionId:\\d+}/answer/version/{versionId:\\d+}', `remark`='获取用户唯一回答版本详情' where `name`='api_answer_version_detail_user';
update `itellyou`.`sys_permission` set `name`='api_answer_version_list', `platform`='2', `type`='1', `method`='1', `data`='/question/{questionId:\\d+}/answer/{answerId:\\d+}/version', `remark`='获取回答版本列表' where `name`='api_answer_version_list';
update `itellyou`.`sys_permission` set `name`='api_answer_version_list_user', `platform`='2', `type`='1', `method`='1', `data`='/question/{questionId:\\d+}/answer/version', `remark`='获取用户唯一回答版本列表' where `name`='api_answer_version_list_user';
insert into `itellyou`.`sys_permission` ( `name`, `platform`, `type`, `method`, `data`, `remark`) values ( 'web_question_public_edit', '1', '0', '0', '', '问题公共编辑');
update `itellyou`.`sys_permission` set `name`='api_question_collab', `platform`='2', `type`='1', `method`='2', `data`='/question/{id:\\d+}/collab', `remark`='获取问题协作编辑数据' where `name`='api_question_collab';
insert into `itellyou`.`sys_role_permission` ( `role_id`, `permission_name`, `created_user_id`, `created_time`, `created_ip`) values ( '3', 'web_question_public_edit', '10012', '1589595996', '16777343');
insert into `itellyou`.`sys_role_permission` ( `role_id`, `permission_name`, `created_user_id`, `created_time`, `created_ip`) values ( '3', 'web_tag_public_edit', '10012', '1589452974', '16777343');
insert into `itellyou`.`sys_permission` ( `name`, `platform`, `type`, `method`, `data`, `remark`) values ( 'api_upload_video_query', '2', '1', '1', '/upload/video/query', '查询视频播放地址');
insert into `itellyou`.`sys_permission` ( `name`, `platform`, `type`, `method`, `data`, `remark`) values ( 'api_upload_video_save', '2', '1', '2', '/upload/video/save', '保存视频');
insert into `itellyou`.`sys_role_permission` ( `role_id`, `permission_name`, `created_user_id`, `created_time`, `created_ip`) values ( '1', 'api_upload_video_query', '10012', '1589600259', '83929280');
insert into `itellyou`.`sys_role_permission` ( `role_id`, `permission_name`, `created_user_id`, `created_time`, `created_ip`) values ( '2', 'api_upload_video_save', '10012', '1589600285', '16777343');
insert into `itellyou`.`sys_role_permission` ( `role_id`, `permission_name`, `created_user_id`, `created_time`, `created_ip`) values ( '3', 'api_upload_video_query', '10012', '1589600219', '83929280');
insert into `itellyou`.`sys_role_permission` ( `role_id`, `permission_name`, `created_user_id`, `created_time`, `created_ip`) values ( '3', 'api_upload_video_save', '10012', '1589600220', '83929280');
insert into `itellyou`.`sys_permission` ( `name`, `platform`, `type`, `method`, `data`, `remark`) values ( 'api_article_paid_read', '2', '1', '3', '/article/{id:\\d+}/paidread', '文章付费阅读设置');
insert into `itellyou`.`sys_role_permission` ( `role_id`, `permission_name`, `created_user_id`, `created_time`, `created_ip`) values ( '2', 'api_article_paid_read', '10012', '1589634533', '16777343');
insert into `itellyou`.`sys_role_permission` ( `role_id`, `permission_name`, `created_user_id`, `created_time`, `created_ip`) values ( '3', 'api_article_paid_read', '10012', '1589634426', '16777343');
insert into `itellyou`.`sys_permission` ( `name`, `platform`, `type`, `method`, `data`, `remark`) values ( 'api_article_paid_read_pay', '2', '1', '2', '/article/{id:\\d+}/paidread', '支付文章付费阅读费用');
insert into `itellyou`.`sys_role_permission` ( `role_id`, `permission_name`, `created_user_id`, `created_time`, `created_ip`) values ( '3', 'api_article_paid_read_pay', '10012', '1589693135', '16777343');
insert into `itellyou`.`sys_role_permission` ( `role_id`, `permission_name`, `created_user_id`, `created_time`, `created_ip`) values ( '2', 'api_article_paid_read_pay', '10012', '1589693165', '16777343');
insert into `itellyou`.`sys_permission` ( `name`, `platform`, `type`, `method`, `data`, `remark`) values ( 'api_answer_paid_read', '2', '1', '3', '/question/{questionId:\\d+}/answer/{id:\\d+}/paidread', '回答付费阅读设置');
insert into `itellyou`.`sys_role_permission` ( `role_id`, `permission_name`, `created_user_id`, `created_time`, `created_ip`) values ( '2', 'api_answer_paid_read', '10012', '1589634533', '16777343');
insert into `itellyou`.`sys_role_permission` ( `role_id`, `permission_name`, `created_user_id`, `created_time`, `created_ip`) values ( '3', 'api_answer_paid_read', '10012', '1589634426', '16777343');
insert into `itellyou`.`sys_permission` ( `name`, `platform`, `type`, `method`, `data`, `remark`) values ( 'api_answer_paid_read_pay', '2', '1', '2', '/question/{questionId:\\d+}/answer/{id:\\d+}/paidread', '回答文章付费阅读费用');
insert into `itellyou`.`sys_role_permission` ( `role_id`, `permission_name`, `created_user_id`, `created_time`, `created_ip`) values ( '3', 'api_answer_paid_read_pay', '10012', '1589693135', '16777343');
insert into `itellyou`.`sys_role_permission` ( `role_id`, `permission_name`, `created_user_id`, `created_time`, `created_ip`) values ( '2', 'api_answer_paid_read_pay', '10012', '1589693165', '16777343');
insert into `itellyou`.`sys_permission` ( `name`, `platform`, `type`, `method`, `data`, `remark`) values ( 'api_reward_log_answer_list', '2', '1', '1', '/reward/answer/list', '获取某个提问下所有回答打赏日志列表');
insert into `itellyou`.`sys_role_permission` ( `role_id`, `permission_name`, `created_user_id`, `created_time`, `created_ip`) values ( '1', 'api_reward_log_answer_list', '10012', '1589634533', '16777343');
insert into `itellyou`.`sys_role_permission` ( `role_id`, `permission_name`, `created_user_id`, `created_time`, `created_ip`) values ( '3', 'api_reward_log_answer_list', '10012', '1589634426', '16777343');
insert into `itellyou`.`user_notification_display_default` ( `action`, `type`, `value`) values ( '14', '4', '1');
insert into `itellyou`.`user_notification_display_default` ( `action`, `type`, `value`) values ( '14', '3', '1');