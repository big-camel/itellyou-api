package com.itellyou.service.collab.impl;

import com.itellyou.dao.collab.CollabConfigDao;
import com.itellyou.dao.collab.CollabInfoDao;
import com.itellyou.model.collab.CollabConfigModel;
import com.itellyou.model.collab.CollabInfoModel;
import com.itellyou.service.collab.CollabInfoService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollabInfoServiceImpl implements CollabInfoService {

    @Autowired
    private CollabInfoDao collabInfoDao;

    @Autowired
    private CollabConfigDao collabConfigDao;

    @Override
    public int insert(CollabInfoModel collabInfoModel) {
        return collabInfoDao.insert(collabInfoModel);
    }

    @Override
    public CollabInfoModel findById(Long id) {
        return collabInfoDao.findById(id);
    }

    @Override
    public CollabInfoModel findByToken(String token) {
        return collabInfoDao.findByToken(token);
    }

    @Override
    public CollabInfoModel createDefault(String key, Long userId, String clientIp) {
        return createByHostKey("default",key,userId,clientIp);
    }

    @Override
    public CollabInfoModel createByHost(String host, String key, Long userId, String clientIp) {
        String token = StringUtils.createToken(key + "_" + userId + "_collab_" + clientIp);
        CollabInfoModel collabInfoModel = new CollabInfoModel(null,key,token,host,false, DateUtils.toLocalDateTime(),userId, IPUtils.toLong(clientIp));
        int rows = collabInfoDao.insert(collabInfoModel);
        if(rows != 1)
            return null;
        return collabInfoModel;
    }

    @Override
    public CollabInfoModel createByHostKey(String hostKey, String key, Long userId, String clientIp) {
        CollabConfigModel collabConfigModel = collabConfigDao.findByKey(hostKey);
        if(collabConfigModel == null){
            return null;
        }
        return createByHost(collabConfigModel.getHost(),key,userId,clientIp);
    }
}
