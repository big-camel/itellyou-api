package com.itellyou.dao.sys;

import com.itellyou.model.sys.SysSettingModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SysSettingDao {

    SysSettingModel findByKey(String key);

    int updateByKey(SysSettingModel model);
}
