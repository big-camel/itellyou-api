package com.itellyou.dao.upload;

import com.itellyou.model.upload.UploadConfigModel;
import com.itellyou.model.upload.UploadType;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Mapper
@Repository
public interface UploadConfigDao {
    UploadConfigModel get(UploadType type);

    @Select("select * from upload_config")
    @MapKey("type")
    Map<String,UploadConfigModel> getAll();
}
