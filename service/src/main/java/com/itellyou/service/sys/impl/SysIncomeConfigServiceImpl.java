package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysIncomeConfigDao;
import com.itellyou.model.sys.SysIncomeConfigModel;
import com.itellyou.service.sys.SysIncomeConfigService;
import org.springframework.stereotype.Service;

@Service
public class SysIncomeConfigServiceImpl implements SysIncomeConfigService {

    private final SysIncomeConfigDao configDao;

    public SysIncomeConfigServiceImpl(SysIncomeConfigDao configDao) {
        this.configDao = configDao;
    }

    @Override
    public int insert(SysIncomeConfigModel model) {
        return configDao.insert(model);
    }

    @Override
    public int updateById(SysIncomeConfigModel model) {
        return configDao.updateById(model);
    }

}
