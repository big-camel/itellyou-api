package com.itellyou.dao.sys;

import com.itellyou.model.sys.SysPath;
import com.itellyou.model.sys.SysPathModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Mapper
@Repository
public interface SysPathDao {
    int insert(SysPathModel model);
    SysPathModel findByPath(String path);
    SysPathModel findByTypeAndId(@Param("type") SysPath type,@Param("id") Long id);
    int updateByTypeAndId(SysPathModel model);
    List<SysPathModel> search(@Param("type") SysPath type, @Param("ids") Collection<Long> ids);
}
