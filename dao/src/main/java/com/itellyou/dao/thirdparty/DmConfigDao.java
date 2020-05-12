package com.itellyou.dao.thirdparty;

import com.itellyou.model.thirdparty.DmConfigModel;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Mapper
@Repository
public interface DmConfigDao {
    @Select("select minute , hour , day from ali_dm_config where type = #{type}")
    DmConfigModel get(String type);

    @Select("select type , minute , hour , day from ali_dm_config")
    @MapKey("type")
    Map<String,DmConfigModel> getAll();
}
