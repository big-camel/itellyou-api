package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysIncomeTipConfigDao;
import com.itellyou.model.sys.SysIncomeTipConfigModel;
import com.itellyou.service.sys.SysIncomeTipConfigService;
import org.springframework.stereotype.Service;

@Service
public class SysIncomeTipConfigServiceImpl implements SysIncomeTipConfigService {

    private final SysIncomeTipConfigDao configDao;

    public SysIncomeTipConfigServiceImpl(SysIncomeTipConfigDao configDao) {
        this.configDao = configDao;
    }

    @Override
    public int insert(SysIncomeTipConfigModel model) {
        return configDao.insert(model);
    }

    @Override
    public int updateById(SysIncomeTipConfigModel model) {
        return configDao.updateById(model);
    }

    @Override
    public int deleteById(Long id) {
        return configDao.deleteById(id);
    }
}
