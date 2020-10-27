package com.itellyou.service.user.bank;

import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserBankType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserBankLogSingleService {
    List<UserBankLogModel> search(Collection<Long> ids,
                                  UserBankType type,
                                  EntityAction action,
                                  EntityType dataType,
                                  String dataKey,
                                  Long userId,
                                  Long beginTime, Long endTime,
                                  Long ip,
                                  Map<String, String> order,
                                  Integer offset,
                                  Integer limit);
}
