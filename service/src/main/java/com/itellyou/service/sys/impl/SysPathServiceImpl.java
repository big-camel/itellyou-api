package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysPathDao;
import com.itellyou.model.sys.SysPath;
import com.itellyou.model.sys.SysPathModel;
import com.itellyou.service.sys.SysPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysPathServiceImpl implements SysPathService {

    private final SysPathDao pathDao;

    @Autowired
    public SysPathServiceImpl(SysPathDao pathDao){
        this.pathDao = pathDao;
    }

    @Override
    public int insert(SysPathModel model) {
        return pathDao.insert(model);
    }

    @Override
    public SysPathModel findByPath(String path) {
        return pathDao.findByPath(path);
    }

    @Override
    public SysPathModel findByTypeAndId(SysPath type, Long id) {
        return pathDao.findByTypeAndId(type,id);
    }

    @Override
    public int updateByTypeAndId(SysPathModel model) {
        return pathDao.updateByTypeAndId(model);
    }
}
