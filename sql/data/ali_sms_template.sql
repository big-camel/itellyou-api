INSERT INTO `ali_sms_template`(`id`, `name`, `code`, `param`, `sign_name`, `expire`) VALUES ('login', '登录验证码模版', 'SMS_86860054', ' {\"code\":\"${code}\"}', 'itellyou', 900);
INSERT INTO `ali_sms_template`(`id`, `name`, `code`, `param`, `sign_name`, `expire`) VALUES ('register', '注册验证码模版', 'SMS_86860052', ' {\"code\":\"${code}\"}', 'itellyou', 900);
INSERT INTO `ali_sms_template`(`id`, `name`, `code`, `param`, `sign_name`, `expire`) VALUES ('replace', '替换手机验证码模版', 'SMS_86860050', ' {\"code\":\"${code}\"}', 'itellyou', 900);
INSERT INTO `ali_sms_template`(`id`, `name`, `code`, `param`, `sign_name`, `expire`) VALUES ('verify', '安全验证验证码模版', 'SMS_86860056', ' {\"code\":\"${code}\"}', 'itellyou', 900);
