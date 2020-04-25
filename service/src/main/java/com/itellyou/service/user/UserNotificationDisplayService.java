package com.itellyou.service.user;

import com.itellyou.model.user.UserOperationalAction;
import com.itellyou.model.user.UserNotificationDisplayModel;
import com.itellyou.model.sys.EntityType;

import java.util.List;

public interface UserNotificationDisplayService {
    int insertOrUpdate(UserNotificationDisplayModel... models);
    List<UserNotificationDisplayModel> searchByDefault(Long userId, UserOperationalAction action, EntityType type);
    UserNotificationDisplayModel findByDefault(Long userId, UserOperationalAction action, EntityType type);
}
