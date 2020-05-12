package com.itellyou.service.common;

import com.itellyou.model.common.NotificationMarkModel;

public interface NotificationMarkService {
    int insertOrUpdate(NotificationMarkModel model);

    NotificationMarkModel findByUserId(Long userId);
}
