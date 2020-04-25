package com.itellyou.service.user;

import com.itellyou.model.user.UserLoginLogModel;

public interface UserLoginLogService {
    int insert(UserLoginLogModel userLoginLogModel);

    int setDisabled(Boolean status,String token);
}
