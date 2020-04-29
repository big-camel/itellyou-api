package com.itellyou.service.user;

import com.itellyou.model.user.UserBankLogModel;
import com.itellyou.model.user.UserBankLogType;
import com.itellyou.model.user.UserBankModel;
import com.itellyou.model.user.UserBankType;

import javax.servlet.http.HttpServletResponse;

public interface UserBankService {
    UserBankModel findByUserId(Long userId);

    UserBankLogModel update(Double amount, UserBankType type, Long userId, String remark, UserBankLogType dataType, String dataKey, Long clientIp) throws Exception;

    UserBankLogModel update(Double amount, UserBankType type, Long userId,String remark,Long clientIp) throws Exception;

    int insert(UserBankModel bankModel);
}
