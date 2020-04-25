package com.itellyou.service.upload.impl;

import com.itellyou.dao.upload.UploadFileDao;
import com.itellyou.model.upload.UploadFileModel;
import com.itellyou.service.upload.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UploadFileServiceImpl implements UploadFileService {

    private final UploadFileDao uploadFileDao;

    @Autowired
    public UploadFileServiceImpl(UploadFileDao uploadFileDao){
        this.uploadFileDao = uploadFileDao;
    }

    @Override
    public int insert(UploadFileModel configModel) {
        return uploadFileDao.insert(configModel);
    }

    @Override
    public List<UploadFileModel> search(String key, Long userId, String extname, String domain, Long minSize, Long maxSize, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return uploadFileDao.search(key,userId,extname,domain,minSize,maxSize,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public UploadFileModel findByKey(String key) {
        List<UploadFileModel> list = search(key,null,null,null,null,null,null,null,null,null,null,null);

        return list != null && list.size() > 0 ? list.get(0) : null;
    }
}
