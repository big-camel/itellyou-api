package com.itellyou.service.software.impl;

import com.itellyou.dao.software.SoftwareGrabDao;
import com.itellyou.model.software.SoftwareGrabModel;
import com.itellyou.service.software.SoftwareGrabService;
import org.springframework.stereotype.Service;

@Service
public class SoftwareGrabServiceImpl implements SoftwareGrabService {

    private final SoftwareGrabDao grabDao;

    public SoftwareGrabServiceImpl(SoftwareGrabDao grabDao) {
        this.grabDao = grabDao;
    }

    @Override
    public SoftwareGrabModel findById(String id) {
        return grabDao.findById(id);
    }

    @Override
    public int update(SoftwareGrabModel model) {
        return grabDao.update(model);
    }
}
