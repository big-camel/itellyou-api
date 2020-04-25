package com.itellyou.dao.ali;

import com.itellyou.model.ali.SmsConfigModel;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Mapper
@Repository
public interface SmsConfigDao {
    @Select("select minute , hour , day from ali_sms_config where type = #{type}")
    SmsConfigModel get(String type);

    @Select("select type , minute , hour , day from ali_sms_config")
    @MapKey("type")
    Map<String,SmsConfigModel> getAll();
}
