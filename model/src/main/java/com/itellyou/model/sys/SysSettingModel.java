package com.itellyou.model.sys;

import com.itellyou.util.CacheEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysSettingModel implements CacheEntity {

    private String key;
    private String name;
    private String logo;
    private String icpText;
    private String copyright;
    private String companyName;
    private String userAgreementLink;
    private String footerScripts;

    @Override
    public String cacheKey() {
        return key;
    }
}
