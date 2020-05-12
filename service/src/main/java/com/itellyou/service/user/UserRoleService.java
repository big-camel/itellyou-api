package com.itellyou.service.user;

import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.model.user.UserRoleModel;

import java.util.List;
import java.util.Map;

public interface UserRoleService {

    int insert(UserRoleModel model);

    int delete(Long userId, Long roleId);

    int deleteByRoleId(Long roleId);

    List<SysRoleModel> findRoleByUserId(Long userId,boolean includeRank);

    List<SysRoleModel> findRoleByUserId(Long userId);

    List<UserRoleModel> search(Long roleId,
                               Long userId,
                               Long beginTime, Long endTime,
                               Long ip,
                               Map<String, String> order,
                               Integer offset,
                               Integer limit);

    int count(Long roleId,
              Long userId,
              Long beginTime, Long endTime,
              Long ip);
}
