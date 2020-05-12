package com.itellyou.service.common;

import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.common.NotificationDisplayModel;
import com.itellyou.model.sys.EntityType;

import java.util.List;

public interface NotificationDisplayService {
    int insertOrUpdate(NotificationDisplayModel... models);
    List<NotificationDisplayModel> searchByDefault(Long userId, EntityAction action, EntityType type);
    NotificationDisplayModel findByDefault(Long userId, EntityAction action, EntityType type);
    List<NotificationDisplayModel> getDefault();
}
