package com.itellyou.service.sys;

import com.itellyou.model.sys.SysIncomeTipConfigModel;

public interface SysIncomeTipConfigService {

    int insert(SysIncomeTipConfigModel model);

    int updateById(SysIncomeTipConfigModel model);

    int deleteById(Long id);
}
