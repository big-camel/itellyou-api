package com.itellyou.service.sys;

import com.itellyou.model.sys.SysPath;
import com.itellyou.model.sys.SysPathModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

public interface SysPathService {
    int insert(SysPathModel model);
    SysPathModel findByPath(String path);
    SysPathModel findByTypeAndId(SysPath type,Long id);
    int updateByTypeAndId(SysPathModel model);
}
