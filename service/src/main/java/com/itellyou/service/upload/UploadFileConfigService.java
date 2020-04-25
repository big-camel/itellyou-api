package com.itellyou.service.upload;

import com.itellyou.model.upload.UploadFileConfigModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UploadFileConfigService {
    List<UploadFileConfigModel> search(Long id,Long userId,String name,
                                       Boolean isImage,Boolean isVideo, Boolean isFile, Boolean isDoc,
                                       Long beginTime, Long endTime, Long ip,
                                       Map<String,String> order,
                                       Integer offset,
                                       Integer limit);

    UploadFileConfigModel findImageConfigByName(String name);

    UploadFileConfigModel findVideoConfigByName(String name);

    UploadFileConfigModel findConfig(String name,Boolean isImage,Boolean isVideo, Boolean isFile, Boolean isDoc);
}
