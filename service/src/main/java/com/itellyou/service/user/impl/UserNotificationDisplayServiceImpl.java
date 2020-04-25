package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserNotificationDisplayDao;
import com.itellyou.model.user.UserOperationalAction;
import com.itellyou.model.user.UserNotificationDisplayModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.user.UserNotificationDisplayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserNotificationDisplayServiceImpl implements UserNotificationDisplayService {

    private final UserNotificationDisplayDao displayDao;

    @Autowired
    public UserNotificationDisplayServiceImpl(UserNotificationDisplayDao displayDao){
        this.displayDao = displayDao;
    }

    @Override
    public int insertOrUpdate(UserNotificationDisplayModel... models) {
        return displayDao.insertOrUpdate(models);
    }

    @Override
    public List<UserNotificationDisplayModel> searchByDefault(Long userId, UserOperationalAction action, EntityType type) {
        return displayDao.searchByDefault(userId,action,type);
    }

    @Override
    public UserNotificationDisplayModel findByDefault(Long userId, UserOperationalAction action, EntityType type) {
        List<UserNotificationDisplayModel> list = searchByDefault(userId,action,type);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }
}
