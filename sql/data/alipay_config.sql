INSERT INTO `alipay_config`(`app_id`, `private_key`, `public_key`, `alipay_key`, `gateway`, `public_cert_path`, `alipay_cert_path`, `root_cert_path`, `notify_url`, `return_url`, `redirect_uri`, `is_enable`, `is_default`) VALUES ('支付宝app编号', '', '', NULL, 'https://openapi.alipay.com/gateway.do', '/cert/appCertPublicKey_xxxxx.crt', '/cert/alipayCertPublicKey_RSA2.crt', '/cert/alipayRootCert.crt', 'https://www.aomao.com/api/pay/alipay/callback', '', 'https://www.aomao.com/api/oauth/alipay/callback', 1, 1);
