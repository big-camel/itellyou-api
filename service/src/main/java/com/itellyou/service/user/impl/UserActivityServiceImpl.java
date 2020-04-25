package com.itellyou.service.user.impl;

import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.*;
import com.itellyou.service.user.UserActivityService;
import com.itellyou.service.user.UserOperationalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserActivityServiceImpl implements UserActivityService {

    private final UserOperationalService operationalService;
    private final Map<UserOperationalAction, HashSet<EntityType>> actionsMap = new LinkedHashMap<>();

    @Autowired
    public UserActivityServiceImpl(UserOperationalService operationalService){
        this.operationalService = operationalService;
        actionsMap.put(UserOperationalAction.FOLLOW,new LinkedHashSet<EntityType>(){{
            //add(UserOperationalType.USER);
            add(EntityType.QUESTION);//关注提问
            add(EntityType.ANSWER);//收藏回答
            add(EntityType.ARTICLE);//收藏文章
            add(EntityType.COLUMN);//关注专栏
            add(EntityType.TAG);//关注标签
        }});
        actionsMap.put(UserOperationalAction.LIKE,new LinkedHashSet<EntityType>(){{
            add(EntityType.ANSWER);//赞同回答
            add(EntityType.ARTICLE);//赞同文章
        }});
        actionsMap.put(UserOperationalAction.PUBLISH,new LinkedHashSet<EntityType>(){{
            add(EntityType.QUESTION);//发布提问
            add(EntityType.ANSWER);//发布回答
            add(EntityType.ARTICLE);//发布文章
        }});
    }

    private Map<UserOperationalAction, HashSet<EntityType>> filterActionsMap(UserOperationalAction action, EntityType type){
        Map<UserOperationalAction, HashSet<EntityType>> map = new LinkedHashMap<>();
        for (Map.Entry<UserOperationalAction,HashSet<EntityType>> entry:actionsMap.entrySet()) {
            HashSet<EntityType> hashSet = new LinkedHashSet<>();
            for (EntityType val:entry.getValue()) {
                if(type == null || val.equals(type)) hashSet.add(val);
            }
            if((action == null || entry.getKey().equals(action)) && !hashSet.isEmpty()) map.put(entry.getKey(),hashSet);
        }
        return map;
    }

    @Override
    public List<UserOperationalDetailModel> search(Long id, UserOperationalAction action, EntityType type, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {

        return operationalService.searchDetail(id,filterActionsMap(action,type),null,userId,null,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(Long id, UserOperationalAction action, EntityType type, Long userId, Long beginTime, Long endTime, Long ip) {
        return operationalService.count(id,filterActionsMap(action,type),null,userId,null,beginTime,endTime,ip);
    }

    @Override
    public PageModel<UserOperationalDetailModel> page(UserOperationalAction action, EntityType type, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<UserOperationalDetailModel> data = search(null,action,type,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(null,action,type,userId,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }
}
