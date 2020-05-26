package com.itellyou.service.sys;

import com.itellyou.model.sys.SysSettingModel;

public interface SysSettingService {

    SysSettingModel findByKey(String key);

    SysSettingModel findByDefault();

    int updateByKey(SysSettingModel model);
}
