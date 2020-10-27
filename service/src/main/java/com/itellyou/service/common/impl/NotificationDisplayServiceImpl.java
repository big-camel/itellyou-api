package com.itellyou.service.common.impl;

import com.itellyou.dao.common.NotificationDisplayDao;
import com.itellyou.model.common.NotificationDisplayModel;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.common.NotificationDisplayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@CacheConfig(cacheNames = CacheKeys.NOTIFICATION_DISPLAY_KEY)
@Service
public class NotificationDisplayServiceImpl implements NotificationDisplayService {

    private final NotificationDisplayDao displayDao;

    @Autowired
    public NotificationDisplayServiceImpl(NotificationDisplayDao displayDao){
        this.displayDao = displayDao;
    }

    @Override
    @CacheEvict(key = "#models[0].userId")
    public int insertOrUpdate(NotificationDisplayModel... models) {
        return displayDao.insertOrUpdate(models);
    }

    @Override
    @CacheEvict(key = "#userId")
    public List<NotificationDisplayModel> searchByDefault(Long userId, EntityAction action, EntityType type) {
        return displayDao.searchByDefault(userId,action,type);
    }

    @Override
    public NotificationDisplayModel findByDefault(Long userId, EntityAction action, EntityType type) {
        List<NotificationDisplayModel> list = searchByDefault(userId,action,type);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    @Override
    @Cacheable(key = "#root.methodName",unless = "#result == null")
    public List<NotificationDisplayModel> getDefault() {
        return displayDao.getDefault();
    }
}
