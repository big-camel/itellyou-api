package com.itellyou.service.thirdparty.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.itellyou.model.thirdparty.AliConfigModel;
import com.itellyou.model.upload.UploadConfigModel;
import com.itellyou.model.upload.UploadType;
import com.itellyou.service.common.ConfigDefaultService;
import com.itellyou.service.thirdparty.OfficePreviewService;
import com.itellyou.service.upload.UploadConfigService;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;

@Service
public class OfficePreviewServiceImpl implements OfficePreviewService {

    private final UploadConfigService configService;
    private final ConfigDefaultService<AliConfigModel> aliConfigService;

    public OfficePreviewServiceImpl(UploadConfigService configService, AliConfigDefaultServiceImpl aliConfigService) {
        this.configService = configService;
        this.aliConfigService = aliConfigService;
    }

    @Override
    public URL GetPreviewURL(String key) throws Exception {
        try {
            AliConfigModel aliConfigModel = aliConfigService.getDefault();
            if (aliConfigModel == null) throw new Exception("未配置aliyun参数");
            UploadConfigModel configModel = configService.get(UploadType.DOC);
            if (configModel == null) throw new Exception("错误的上传配置参数");
            String process = "imm/previewdoc,copy_1";
            OSS client = new OSSClientBuilder().build(configModel.getEndpoint(), aliConfigModel.getId(), aliConfigModel.getSecret());
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(configModel.getBucket(), key);
            request.setProcess(process);
            request.setExpiration(new Date(new Date().getTime() + 3600 * 1000));
            return client.generatePresignedUrl(request);
        }catch (Exception e){
            throw e;
        }
    }
}
