package com.itellyou.service.thirdparty.impl;

import com.itellyou.dao.thirdparty.DmTemplateDao;
import com.itellyou.model.thirdparty.DmTemplateModel;
import com.itellyou.service.common.ConfigMapService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DmTemplateServiceImpl implements ConfigMapService<DmTemplateModel> {

    private final DmTemplateDao dmTemplateDao;

    public DmTemplateServiceImpl(DmTemplateDao dmTemplateDao) {
        this.dmTemplateDao = dmTemplateDao;
    }

    @Override
    public DmTemplateModel find(String id) {
        return dmTemplateDao.findById(id);
    }

    @Override
    public Map<String, DmTemplateModel> getMap() {
        return dmTemplateDao.getAll();
    }
}
