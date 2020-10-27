package com.itellyou.service.sys;

import com.itellyou.model.sys.SysPath;
import com.itellyou.model.sys.SysPathModel;

import java.util.Collection;
import java.util.List;

public interface SysPathService {
    int insert(SysPathModel model);
    SysPathModel findByPath(String path);
    SysPathModel findByTypeAndId(SysPath type,Long id);
    int updateByTypeAndId(SysPathModel model);
    List<SysPathModel> search(SysPath type, Collection<Long> ids);
}
