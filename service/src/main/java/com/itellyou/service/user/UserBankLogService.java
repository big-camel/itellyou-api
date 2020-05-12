package com.itellyou.service.user;

import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserBankType;

import java.util.List;
import java.util.Map;

public interface UserBankLogService {
    int insert(UserBankLogModel userBankLogModel);

    List<UserBankLogModel> search(Long id,
                                  UserBankType type,
                                  EntityAction action,
                                  EntityType dataType,
                                  String dataKey,
                                  Long userId,
                                  Long beginTime,Long endTime,
                                  Long ip,
                                  Map<String, String> order,
                                  Integer offset,
                                  Integer limit);

    int count(Long id,
              UserBankType type,
              EntityAction action,
              EntityType dataType,
              String dataKey,
              Long userId,
              Long beginTime,Long endTime,
              Long ip);

    double total(Long id,
              UserBankType type,
              EntityAction action,
              EntityType dataType, String dataKey,
              Long userId,
              Long beginTime,Long endTime,
              Long ip);

    PageModel<UserBankLogModel> page(Long id,
                                     UserBankType type,
                                     EntityAction action,
                                     EntityType dataType,
                                     String dataKey,
                                     Long userId,
                                     Long beginTime,Long endTime,
                                     Long ip,
                                     Map<String, String> order,
                                     Integer offset,
                                     Integer limit);
}
