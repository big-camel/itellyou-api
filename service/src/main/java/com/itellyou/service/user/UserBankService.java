package com.itellyou.service.user;

import com.itellyou.model.user.UserBankLogType;
import com.itellyou.model.user.UserBankModel;
import com.itellyou.model.user.UserBankType;

public interface UserBankService {
    UserBankModel findByUserId(Long userId);

    int update(Double amount, UserBankType type, Long userId, String remark, UserBankLogType dataType, String dataKey, Long clientIp);

    int update(Double amount, UserBankType type, Long userId,String remark,Long clientIp);

    int insert(UserBankModel bankModel);
}
