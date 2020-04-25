package com.itellyou.service.user;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserOperationalAction;
import com.itellyou.model.user.UserOperationalDetailModel;
import com.itellyou.model.sys.EntityType;

import java.util.List;
import java.util.Map;

public interface UserActivityService {

    List<UserOperationalDetailModel> search(Long id,
                                            UserOperationalAction action,
                                            EntityType type,
                                            Long userId,
                                            Long beginTime, Long endTime,
                                            Long ip,
                                            Map<String, String> order,
                                            Integer offset,
                                            Integer limit);
    int count(Long id,
              UserOperationalAction action,
              EntityType type,
              Long userId,
              Long beginTime,Long endTime,
              Long ip);

    PageModel<UserOperationalDetailModel> page(UserOperationalAction action,
                                            EntityType type,
                                            Long userId,
                                            Long beginTime,Long endTime,
                                            Long ip,
                                            Map<String, String> order,
                                            Integer offset,
                                            Integer limit);
}
