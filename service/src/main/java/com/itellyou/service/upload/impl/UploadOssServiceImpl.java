package com.itellyou.service.upload.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.itellyou.model.ali.AliConfigModel;
import com.itellyou.model.upload.*;
import com.itellyou.service.ali.AliConfigService;
import com.itellyou.service.upload.UploadConfigService;
import com.itellyou.service.upload.UploadFileConfigService;
import com.itellyou.service.upload.UploadFileService;
import com.itellyou.service.upload.UploadOssService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@Service
public class UploadOssServiceImpl implements UploadOssService {

    private final UploadConfigService configService;
    private final AliConfigService aliConfigService;
    private final UploadFileConfigService fileConfigService;
    private final UploadFileService fileService;

    @Autowired
    public UploadOssServiceImpl(UploadConfigService configService,AliConfigService aliConfigService,UploadFileConfigService fileConfigService,UploadFileService fileService){
        this.configService = configService;
        this.aliConfigService = aliConfigService;
        this.fileConfigService = fileConfigService;
        this.fileService = fileService;
    }

    @Override
    @Transactional
    public UploadFileModel uploadFile(UploadType type,Long userId, String name, Long size, InputStream fileStream, UploadSource source, String ip) throws Exception {
        AliConfigModel aliConfigModel = aliConfigService.get();
        if(aliConfigModel == null) throw new Exception("未配置aliyun参数");
        UploadConfigModel configModel = configService.get(type);
        if(configModel == null) throw new Exception("错误的上传配置参数");
        if(size / 1024 > configModel.getSize()) throw new Exception("超过 " + configModel.getSize() + " kb 上传限制");

        try {
            String extname = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
            UploadFileConfigModel fileConfigModel = fileConfigService.findConfig(extname,UploadType.IMAGE.equals(type) ? true : null,
                    UploadType.VIDEO.equals(type) ? true : null,
                    UploadType.FILE.equals(type) ? true : null,
                    UploadType.DOC.equals(type) ? true : null);
            if (fileConfigModel == null) throw new Exception("不支持的文件格式 " + extname);
            String key = userId + "/" + source.getName() + "/" + DateUtils.format("yyyy/MM/dd") + "/" + DateUtils.getTimestamp() + "-" + UUID.randomUUID() + "." + extname;

            UploadFileModel fileModel = new UploadFileModel(key, name, extname, configModel.getDomain(), configModel.getBucket(), source, size, DateUtils.getTimestamp(), userId, IPUtils.toLong(ip));
            int result = fileService.insert(fileModel);
            if (result != 1) throw new Exception("写入上传记录失败");

            OSS ossClient = new OSSClientBuilder().build(configModel.getEndpoint(), aliConfigModel.getId(), aliConfigModel.getSecret());
            ossClient.putObject(configModel.getBucket(), key, fileStream);
            ossClient.shutdown();
            return fileModel;
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
    }
}
