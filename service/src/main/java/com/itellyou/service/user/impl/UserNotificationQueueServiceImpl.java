package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserNotificationQueueDao;
import com.itellyou.model.user.UserOperationalModel;
import com.itellyou.service.user.UserNotificationQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserNotificationQueueServiceImpl implements UserNotificationQueueService {

    private final UserNotificationQueueDao queueDao;

    @Autowired
    public UserNotificationQueueServiceImpl(UserNotificationQueueDao queueDao){
        this.queueDao = queueDao;
    }

    @Override
    public List<UserOperationalModel> searchFollowUser(Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return queueDao.searchFollowUser(userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public List<UserOperationalModel> searchPublishAnswer(Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return queueDao.searchPublishAnswer(userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public List<UserOperationalModel> searchPublishArticle(Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return queueDao.searchPublishArticle(userId,beginTime,endTime,ip,order,offset,limit);
    }
}
