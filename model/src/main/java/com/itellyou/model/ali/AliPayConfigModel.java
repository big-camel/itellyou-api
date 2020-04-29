package com.itellyou.model.ali;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayConfigModel {
    private String appId;
    private String privateKey;
    private String publicKey;
    private String alipayKey;
    private String gateway;
    private String publicCertPath;
    private String alipayCertPath;
    private String rootCertPath;
    private String notifyUrl;
    private String returnUrl;
    private String redirectUri;
    private boolean isEnable;
    private boolean isDefault;
}
