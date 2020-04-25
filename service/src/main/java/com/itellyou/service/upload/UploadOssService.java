package com.itellyou.service.upload;

import com.itellyou.model.upload.UploadFileModel;
import com.itellyou.model.upload.UploadSource;
import com.itellyou.model.upload.UploadType;

import java.io.InputStream;

public interface UploadOssService {

    UploadFileModel uploadFile(UploadType type, Long userId, String name, Long size, InputStream fileStream, UploadSource source, String ip) throws Exception;
}
