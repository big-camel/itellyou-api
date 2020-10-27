package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysIncomeRelatedDao;
import com.itellyou.model.sys.SysIncomeRelatedModel;
import com.itellyou.service.sys.SysIncomeRelatedService;
import org.springframework.stereotype.Service;

@Service
public class SysIncomeRelatedServiceImpl implements SysIncomeRelatedService {

    private final SysIncomeRelatedDao relatedDao;

    public SysIncomeRelatedServiceImpl(SysIncomeRelatedDao relatedDao) {
        this.relatedDao = relatedDao;
    }

    @Override
    public int insertModels(SysIncomeRelatedModel... models) {
        return relatedDao.insertModels(models);
    }
}
