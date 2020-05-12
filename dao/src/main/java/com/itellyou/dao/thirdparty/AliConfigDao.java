package com.itellyou.dao.thirdparty;

import com.itellyou.model.thirdparty.AliConfigModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AliConfigDao {

    @Select("select id,secret from ali_config limit 0,1")
    AliConfigModel get();
}
