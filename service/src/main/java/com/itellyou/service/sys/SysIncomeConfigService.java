package com.itellyou.service.sys;

import com.itellyou.model.sys.SysIncomeConfigModel;

public interface SysIncomeConfigService {

    int insert(SysIncomeConfigModel model);

    int updateById(SysIncomeConfigModel model);
}
