package com.itellyou.service.user;

import com.itellyou.model.sys.SysRoleModel;
import com.itellyou.model.user.UserRankRoleModel;

import java.util.List;
import java.util.Map;

public interface UserRankRoleService {

    int insert(UserRankRoleModel model);

    int delete(Long rankId, Long roleId);

    int deleteByRoleId(Long roleId);

    List<SysRoleModel> findRoleByRankId(Long rankId);

    List<SysRoleModel> findRoleByUserId(Long userId);

    List<UserRankRoleModel> search(Long roleId,
                               Long rankId,
                               Long beginTime, Long endTime,
                               Long ip,
                               Map<String, String> order,
                               Integer offset,
                               Integer limit);

    int count(Long roleId,
              Long rankId,
              Long beginTime, Long endTime,
              Long ip);
}
