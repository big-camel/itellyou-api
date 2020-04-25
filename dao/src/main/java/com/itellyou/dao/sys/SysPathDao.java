package com.itellyou.dao.sys;

import com.itellyou.model.sys.SysPath;
import com.itellyou.model.sys.SysPathModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SysPathDao {
    int insert(SysPathModel model);
    SysPathModel findByPath(String path);
    SysPathModel findByTypeAndId(@Param("type") SysPath type,@Param("id") Long id);
    int updateByTypeAndId(SysPathModel model);
}
