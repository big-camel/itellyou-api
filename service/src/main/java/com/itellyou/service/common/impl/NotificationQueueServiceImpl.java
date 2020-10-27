package com.itellyou.service.common.impl;

import com.itellyou.dao.common.NotificationQueueDao;
import com.itellyou.model.common.NotificationQueueModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.service.common.NotificationQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class NotificationQueueServiceImpl implements NotificationQueueService {

    private final NotificationQueueDao queueDao;

    @Autowired
    public NotificationQueueServiceImpl(NotificationQueueDao queueDao){
        this.queueDao = queueDao;
    }

    @Override
    public int insert(NotificationQueueModel model) {
        return queueDao.insert(model);
    }

    @Override
    public List<NotificationQueueModel> search(Long id, Map<EntityAction, Collection<EntityType>> actionsMap, Long targetUserId, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return queueDao.search(id,actionsMap,targetUserId,userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(Long id, Map<EntityAction, Collection<EntityType>> actionsMap, Long targetUserId, Long userId, Long beginTime, Long endTime, Long ip) {
        return queueDao.count(id,actionsMap,targetUserId,userId,beginTime,endTime,ip);
    }

    @Override
    public List<NotificationQueueModel> find(EntityAction action, EntityType type, Long userId, Long targetId) {
        return queueDao.find(action,type,userId,targetId);
    }

    @Override
    public int update(Long id, Long createdTime, Long createdIp) {
        return queueDao.update(id,createdTime,createdIp);
    }

    @Override
    public List<NotificationQueueModel> searchFollowUser(Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return queueDao.searchFollowUser(userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public List<NotificationQueueModel> searchPublishAnswer(Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return queueDao.searchPublishAnswer(userId,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public List<NotificationQueueModel> searchPublishArticle(Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return queueDao.searchPublishArticle(userId,beginTime,endTime,ip,order,offset,limit);
    }
}
