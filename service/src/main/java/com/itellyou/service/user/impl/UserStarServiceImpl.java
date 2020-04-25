package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserStarDao;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.*;
import com.itellyou.service.user.*;
import com.itellyou.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@Service
public class UserStarServiceImpl implements UserStarService {

    private final UserStarDao starDao;
    private final UserInfoService infoService;
    private final UserSearchService searchService;
    private final UserOperationalService operationalService;

    @Autowired
    public UserStarServiceImpl(UserStarDao starDao, UserInfoService infoService,UserSearchService searchService, UserOperationalService operationalService){
        this.starDao = starDao;
        this.infoService = infoService;
        this.searchService = searchService;
        this.operationalService = operationalService;
    }

    @Override
    @Transactional
    public int insert(UserStarModel model) throws Exception {
        try{
            int result = starDao.insert(model);
            if(result != 1) throw new Exception("写入关注记录失败");
            result = infoService.updateStarCount(model.getCreatedUserId(),1);
            if(result != 1) throw new Exception("更新关注数失败");
            result = infoService.updateFollowerCount(model.getUserId(),1);
            if(result != 1) throw new Exception("更新被关注数失败");
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        operationalService.insertAsync(new UserOperationalModel(UserOperationalAction.FOLLOW, EntityType.USER,model.getUserId(),model.getUserId(),model.getCreatedUserId(), DateUtils.getTimestamp(),model.getCreatedIp()));
        return 1;
    }

    @Override
    @Transactional
    public int delete(Long userId, Long followerId) throws Exception {
        try{
            int result = starDao.delete(userId,followerId);
            if(result != 1) throw new Exception("删除关注记录失败");
            result = infoService.updateStarCount(followerId,-1);
            if(result != 1) throw new Exception("更新关注数失败");
            result = infoService.updateFollowerCount(userId,-1);
            if(result != 1) throw new Exception("更新被关注数失败");
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw e;
        }
        operationalService.deleteByTargetIdAsync(UserOperationalAction.FOLLOW, EntityType.USER,followerId,userId);
        return 1;
    }

    @Override
    public List<UserStarDetailModel> search(Long userId, Long followerId,Long searchId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<UserStarModel> models = starDao.search(userId,followerId,beginTime,endTime,ip,order,offset,limit);
        List<UserStarDetailModel> detailModels = new ArrayList<>();
        HashSet<Long> userHash = new LinkedHashSet<>();
        for (UserStarModel model : models){
            if(!userHash.contains(model.getUserId())){
                userHash.add(model.getUserId());
            }
            if(!userHash.contains(model.getCreatedUserId())){
                userHash.add(model.getCreatedUserId());
            }
            UserStarDetailModel detailModel = new UserStarDetailModel(model);
            detailModels.add(detailModel);
        }
        List<UserDetailModel> userDetailModels = searchService.search(userHash,searchId,null,null,null,null,null,null,null,null,null,null);
        for (UserStarDetailModel detailModel : detailModels){
            for (UserDetailModel user : userDetailModels){
                if(detailModel.getUserId().equals(user.getId())){
                    detailModel.setStar(user);
                }
                if(detailModel.getCreatedUserId().equals(user.getId())){
                    detailModel.setFollower(user);
                }
            }
        }
        return detailModels;
    }

    @Override
    public int count(Long userId, Long followerId, Long beginTime, Long endTime, Long ip) {
        return starDao.count(userId,followerId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<UserStarDetailModel> page(Long userId, Long followerId,Long searchId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<UserStarDetailModel> data = search(userId,followerId,searchId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(userId,followerId,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }
}
