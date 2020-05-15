INSERT INTO `sys_permission` VALUES ('web_tag_public_edit', '1', '0', '0', null, '标签公共编辑')
delete from `sys_permission` where `name` = 'api_tag_collab';
INSERT INTO `sys_permission` VALUES ('api_tag_collab', '2', '1', '2', '/tag/{id:\d+}/collab', '获取标签协作编辑数据');