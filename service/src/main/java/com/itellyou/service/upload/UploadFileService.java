package com.itellyou.service.upload;

import com.itellyou.model.upload.UploadFileModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UploadFileService {

    int insert(UploadFileModel configModel);

    List<UploadFileModel> search(String key, Long userId,String extname,String domain,
                                 Long minSize,Long maxSize,
                                 Long beginTime,Long endTime,Long ip,
                                 Map<String, String> order,
                                 Integer offset,
                                 Integer limit);

    UploadFileModel findByKey(String key);
}
