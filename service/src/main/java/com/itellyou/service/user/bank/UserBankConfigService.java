package com.itellyou.service.user.bank;

import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserBankConfigModel;
import com.itellyou.model.user.UserBankType;

import java.util.List;

public interface UserBankConfigService {

    int insert(UserBankConfigModel model);

    int update(UserBankConfigModel model);

    UserBankConfigModel find(UserBankType bankType, EntityAction action, EntityType type);

    List<UserBankConfigModel> findByType(UserBankType bankType);
}
