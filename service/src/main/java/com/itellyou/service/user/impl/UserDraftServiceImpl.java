package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserDraftDao;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.*;
import com.itellyou.service.user.UserDraftService;
import com.itellyou.service.user.UserOperationalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserDraftServiceImpl implements UserDraftService {

    private final UserDraftDao draftDao;
    private final UserOperationalService operationalService;

    @Autowired
    public UserDraftServiceImpl(UserDraftDao draftDao, UserOperationalService operationalService){
        this.draftDao = draftDao;
        this.operationalService = operationalService;
    }

    @Override
    public int insert(UserDraftModel draftModel) {
        return draftDao.insert(draftModel);
    }

    @Override
    public int insertOrUpdate(UserDraftModel draftModel) {
        return draftDao.insertOrUpdate(draftModel);
    }

    @Override
    public boolean exists(Long userId, EntityType dataType, Long dataKey) {
        return draftDao.exists(userId , dataType,dataKey) == 1;
    }

    @Override
    public int delete(Long userId, EntityType dataType,Long dataKey) {
        return draftDao.delete(userId,dataType,dataKey);
    }

    @Override
    public List<UserDraftDetailModel> search(Long authorId, EntityType dataType, Long dataKey,Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<UserDraftDetailModel> dataList = draftDao.search(authorId,dataType,dataKey,userId,beginTime,endTime,ip,order,offset,limit);
        List<UserOperationalModel> operationalModels = new ArrayList<>();
        for (UserDraftDetailModel model : dataList){
            operationalModels.add(new UserOperationalModel(UserOperationalAction.DEFAULT,model.getDataType(),model.getDataKey(),null,null,null,null));
        }
        List<UserOperationalDetailModel> resultModels = operationalService.toDetail(operationalModels,userId);
        for (int i = 0; i < resultModels.size(); i++) {
            dataList.get(i).setTarget(resultModels.get(i).getTarget());
        }
        return dataList;
    }

    @Override
    public int count(Long authorId, EntityType dataType, Long dataKey,Long userId, Long beginTime, Long endTime, Long ip) {
        return draftDao.count(authorId,dataType,dataKey,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<UserDraftDetailModel> page(Long authorId, EntityType dataType, Long dataKey,Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<UserDraftDetailModel> data = search(authorId,dataType,dataKey,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(authorId,dataType,dataKey,userId,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }
}
