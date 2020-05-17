package com.itellyou.service.sys;

import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.sys.RewardLogDetailModel;
import com.itellyou.model.sys.RewardLogModel;
import com.itellyou.model.user.UserBankType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface RewardLogService {

    int insert(RewardLogModel model);

    List<RewardLogDetailModel> search(Long id,
                                      UserBankType bankType,
                                      EntityType dataType,
                                      HashSet<Long> dataKeys,
                                      Long searchUserId,
                                      Long userId,
                                      Long createdUserId,
                                      Long beginTime, Long endTime,
                                      Long ip,
                                      Map<String, String> order,
                                      Integer offset,
                                      Integer limit);
    int count( Long id,
              UserBankType bankType,
               EntityType dataType,
               HashSet<Long> dataKeys,
               Long userId,
               Long createdUserId,
               Long beginTime,  Long endTime,
               Long ip);

    PageModel<RewardLogDetailModel> page(Long id,
                                    UserBankType bankType,
                                    EntityType dataType, HashSet<Long> dataKeys, Long searchUserId,
                                    Long userId,
                                    Long createdUserId,
                                    Long beginTime,  Long endTime,
                                    Long ip,
                                    Map<String, String> order,
                                    Integer offset,
                                    Integer limit);

    RewardLogDetailModel find(Long id,Long searchUserId);
}
