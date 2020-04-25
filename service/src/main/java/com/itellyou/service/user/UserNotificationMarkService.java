package com.itellyou.service.user;

import com.itellyou.model.user.UserNotificationMarkModel;

public interface UserNotificationMarkService {
    int insertOrUpdate(UserNotificationMarkModel model);

    UserNotificationMarkModel findByUserId(Long userId);
}
