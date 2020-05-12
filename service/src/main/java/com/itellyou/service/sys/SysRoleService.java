package com.itellyou.service.sys;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.SysRoleModel;

import java.util.List;
import java.util.Map;

public interface SysRoleService {

    int insert(SysRoleModel model);

    SysRoleModel findByName(String name,Long userId);

    SysRoleModel findById(Long id);

    int delete(Long id,Long userId) throws Exception;

    int update(Long id,String name,Boolean disabled,String description);

    List<SysRoleModel> search(Long id,
                              String name,
                              Boolean disabled,
                              Boolean system,
                              Long userId,
                              Long beginTime, Long endTime,
                              Long ip,
                              Map<String, String> order,
                              Integer offset,
                              Integer limit);

    int count(Long id,
              String name,
              Boolean disabled,
              Boolean system,
              Long userId,
              Long beginTime, Long endTime,
              Long ip);

    PageModel<SysRoleModel> page(Long id,
                                 String name,
                                 Boolean disabled,
                                 Boolean system,
                                 Long userId,
                                 Long beginTime, Long endTime,
                                 Long ip,
                                 Map<String, String> order,
                                 Integer offset,
                                 Integer limit);
}
