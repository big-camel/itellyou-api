package com.itellyou.service.user.bank;

import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserBankType;

import java.util.Set;

public interface UserBankLogService {
    int insert(UserBankLogModel userBankLogModel);

    double total(Set<Long> ids,
              UserBankType type,
                 EntityAction action,
                 EntityType dataType, String dataKey,
                 Long userId,
                 Long beginTime, Long endTime,
                 Long ip);
}
