package com.itellyou.service.user;

import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserBankLogType;
import com.itellyou.model.user.UserBankType;

import java.util.List;
import java.util.Map;

public interface UserBankLogService {
    int insert(UserBankLogModel userBankLogModel);

    List<UserBankLogModel> search(String id,
                                  UserBankType type,
                                  UserBankLogType dataType,
                                  Long userId,
                                  Long beginTime,Long endTime,
                                  Long ip,
                                  Map<String, String> order,
                                  Integer offset,
                                  Integer limit);

    int count(String id,
              UserBankType type,
              UserBankLogType dataType,
              Long userId,
              Long beginTime,Long endTime,
              Long ip);

    PageModel<UserBankLogModel> page(String id,
                                     UserBankType type,
                                     UserBankLogType dataType,
                                     Long userId,
                                     Long beginTime,Long endTime,
                                     Long ip,
                                     Map<String, String> order,
                                     Integer offset,
                                     Integer limit);
}
