package com.itellyou.service.ali.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.itellyou.model.ali.AliConfigModel;
import com.itellyou.model.upload.UploadConfigModel;
import com.itellyou.model.upload.UploadFileConfigModel;
import com.itellyou.model.upload.UploadFileModel;
import com.itellyou.model.upload.UploadType;
import com.itellyou.service.ali.AliConfigService;
import com.itellyou.service.ali.OfficePreviewService;
import com.itellyou.service.upload.UploadConfigService;
import com.itellyou.service.upload.UploadFileConfigService;
import com.itellyou.service.upload.UploadFileService;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;

@Service
public class OfficePreviewServiceImpl implements OfficePreviewService {

    private final UploadConfigService configService;
    private final AliConfigService aliConfigService;

    public OfficePreviewServiceImpl(UploadConfigService configService, AliConfigService aliConfigService) {
        this.configService = configService;
        this.aliConfigService = aliConfigService;
    }

    @Override
    public URL GetPreviewURL(String key) throws Exception {
        try {
            AliConfigModel aliConfigModel = aliConfigService.get();
            if (aliConfigModel == null) throw new Exception("未配置aliyun参数");
            UploadConfigModel configModel = configService.get(UploadType.DOC);
            if (configModel == null) throw new Exception("错误的上传配置参数");
            String process = "imm/previewdoc,copy_1";
            OSS client = new OSSClientBuilder().build(configModel.getEndpoint(), aliConfigModel.getId(), aliConfigModel.getSecret());
            GetObjectRequest getObjectRequest = new GetObjectRequest(configModel.getBucket(), key);
            getObjectRequest.setProcess(process);
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(configModel.getBucket(), key);
            request.setProcess(process);
            request.setExpiration(new Date(new Date().getTime() + 3600 * 1000));
            return client.generatePresignedUrl(request);
        }catch (Exception e){
            throw e;
        }
    }
}
