package com.itellyou.dao.upload;

import com.itellyou.model.upload.UploadFileModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UploadFileDao {

    int insert(UploadFileModel configModel);

    List<UploadFileModel> search(@Param("key") String key, @Param("userId") Long userId, @Param("extname") String extname, @Param("domain") String domain,
                                    @Param("minSize") Long minSize, @Param("maxSize") Long maxSize,
                                       @Param("beginTime") Long beginTime, @Param("endTime") Long endTime, @Param("ip") Long ip,
                                       @Param("order") Map<String, String> order,
                                       @Param("offset") Integer offset,
                                       @Param("limit") Integer limit);

}
