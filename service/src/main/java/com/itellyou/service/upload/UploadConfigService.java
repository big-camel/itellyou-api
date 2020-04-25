package com.itellyou.service.upload;

import com.itellyou.model.upload.UploadConfigModel;
import com.itellyou.model.upload.UploadType;

import java.util.Map;

public interface UploadConfigService {
    UploadConfigModel get(UploadType type);

    Map<String,UploadConfigModel> getAll();
}
