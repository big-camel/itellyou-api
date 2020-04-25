package com.itellyou.dao.ali;

import com.itellyou.model.ali.SmsTemplateModel;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Mapper
@Repository
public interface SmsTemplateDao {
    SmsTemplateModel findById(String id);

    @MapKey("id")
    Map<String,SmsTemplateModel> getAll();
}
