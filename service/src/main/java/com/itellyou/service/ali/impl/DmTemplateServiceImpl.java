package com.itellyou.service.ali.impl;

import com.itellyou.dao.ali.DmTemplateDao;
import com.itellyou.model.ali.DmTemplateModel;
import com.itellyou.service.ali.DmTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DmTemplateServiceImpl implements DmTemplateService {

    @Autowired
    private DmTemplateDao dmTemplateDao;

    @Override
    public DmTemplateModel findById(String id) {
        return dmTemplateDao.findById(id);
    }

    @Override
    public Map<String, DmTemplateModel> getAll() {
        return dmTemplateDao.getAll();
    }
}
