package com.itellyou.dao.thirdparty;

import com.itellyou.model.thirdparty.DmTemplateModel;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Mapper
@Repository
public interface DmTemplateDao {
    DmTemplateModel findById(String id);

    @MapKey("id")
    Map<String,DmTemplateModel> getAll();
}
