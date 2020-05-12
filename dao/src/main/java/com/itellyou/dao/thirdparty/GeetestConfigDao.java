package com.itellyou.dao.thirdparty;

import com.itellyou.model.thirdparty.GeetestConfigModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface GeetestConfigDao {

    @Select("select id,`key` from geetest_config limit 0,1")
    GeetestConfigModel getConfig();
}
