package com.itellyou.service.upload.impl;

import com.itellyou.dao.upload.UploadFileConfigDao;
import com.itellyou.model.upload.UploadFileConfigModel;
import com.itellyou.service.upload.UploadFileConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UploadFileConfigServiceImpl implements UploadFileConfigService {

    private final UploadFileConfigDao uploadFileConfigDao;

    @Autowired
    public UploadFileConfigServiceImpl(UploadFileConfigDao uploadFileConfigDao){
        this.uploadFileConfigDao = uploadFileConfigDao;
    }

    @Override
    public List<UploadFileConfigModel> search(Long id, Long userId, String name, Boolean isImage, Boolean isVideo, Boolean isFile, Boolean isDoc, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return uploadFileConfigDao.search(id,userId,name,isImage,isVideo,isFile,isDoc,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public UploadFileConfigModel findImageConfigByName(String name) {
        List<UploadFileConfigModel> list = search(null,null,name,true,null,null,null,null,null,null,null,null,null);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public UploadFileConfigModel findVideoConfigByName(String name) {
        List<UploadFileConfigModel> list = search(null,null,name,null,true,null,null,null,null,null,null,null,null);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public UploadFileConfigModel findConfig(String name, Boolean isImage, Boolean isVideo, Boolean isFile, Boolean isDoc) {
        List<UploadFileConfigModel> list = search(null,null,name,isImage,isVideo,isFile,isDoc,null,null,null,null,null,null);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }
}
