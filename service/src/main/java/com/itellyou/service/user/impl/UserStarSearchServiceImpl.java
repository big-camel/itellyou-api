package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserStarDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.model.user.UserStarDetailModel;
import com.itellyou.model.user.UserStarModel;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.service.user.UserStarSearchService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserStarSearchServiceImpl implements UserStarSearchService {

    private final UserStarDao starDao;
    private final UserSearchService searchService;

    public UserStarSearchServiceImpl(UserStarDao starDao, UserSearchService searchService) {
        this.starDao = starDao;
        this.searchService = searchService;
    }

    @Override
    public int count(Long userId, Long followerId, Long beginTime, Long endTime, Long ip) {
        return starDao.count(userId,followerId, beginTime,endTime,ip);
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
    public PageModel<UserStarDetailModel> page(Long userId, Long followerId, Long searchId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<UserStarDetailModel> data = search(userId,followerId,searchId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(userId,followerId,beginTime,endTime,ip);
        return new PageModel<>(offset == 0,offset + limit >= total,offset,limit,total,data);
    }

    @Override
    public UserStarDetailModel find(Long userId, Long followerId) {
        List<UserStarDetailModel> data = search(userId,followerId,null,null,null,null,null,null,null);
        return data != null && data.size() > 0 ? data.get(0) : null;
    }
}
