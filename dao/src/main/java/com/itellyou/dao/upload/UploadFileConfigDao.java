package com.itellyou.dao.upload;

import com.itellyou.model.upload.UploadFileConfigModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UploadFileConfigDao {

    int insert(UploadFileConfigModel configModel);

    List<UploadFileConfigModel> search(@Param("id") Long id, @Param("userId") Long userId, @Param("name") String name,
                                       @Param("isImage")  Boolean isImage, @Param("isVideo")  Boolean isVideo, @Param("isFile") Boolean isFile, @Param("isDoc") Boolean isDoc,
                                       @Param("beginTime") Long beginTime, @Param("endTime") Long endTime, @Param("ip") Long ip,
                                       @Param("order") Map<String,String> order,
                                       @Param("offset") Integer offset,
                                       @Param("limit") Integer limit);

}
