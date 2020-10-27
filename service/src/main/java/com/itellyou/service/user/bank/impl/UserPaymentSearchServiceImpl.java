package com.itellyou.service.user.bank.impl;

import com.itellyou.dao.user.UserPaymentDao;
import com.itellyou.model.sys.EntityDataModel;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.*;
import com.itellyou.service.sys.EntityService;
import com.itellyou.service.user.bank.UserPaymentSearchService;
import com.itellyou.service.user.bank.UserPaymentSingleService;
import com.itellyou.util.CacheEntity;
import com.itellyou.util.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserPaymentSearchServiceImpl implements UserPaymentSearchService {

    private final UserPaymentDao paymentDao;
    private final UserPaymentSingleService singleService;
    private final EntityService entityService;

    public UserPaymentSearchServiceImpl(UserPaymentDao paymentDao, UserPaymentSingleService singleService, EntityService entityService) {
        this.paymentDao = paymentDao;
        this.singleService = singleService;
        this.entityService = entityService;
    }

    @Override
    public List<UserPaymentDetailModel> search(Collection<String> ids, UserPaymentStatus status, UserPaymentType type, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        List<UserPaymentModel> list = singleService.search(ids,status,type,userId,beginTime,endTime,ip,order,offset,limit);
        EntityDataModel<CacheEntity> entityDataModel = entityService.search(EntityType.USER,"ids",RedisUtils.getKeys(list,(UserPaymentModel model) -> model.getCreatedUserId()));
        List<UserPaymentDetailModel> detailModels = new LinkedList<>();
        if(list.size() == 0) return detailModels;
        for (UserPaymentModel paymentModel : list){
            UserPaymentDetailModel detailModel = new UserPaymentDetailModel(paymentModel);
            UserDetailModel userModel = entityDataModel.get(EntityType.USER,paymentModel.getCreatedUserId(),model -> model.getCreatedUserId());
            detailModel.setUser(userModel);
            detailModels.add(detailModel);
        }
        return detailModels;
    }

    @Override
    public int count(Collection<String> ids, UserPaymentStatus status, UserPaymentType type, Long userId, Long beginTime, Long endTime, Long ip) {
        return paymentDao.count(ids,status,type,userId,beginTime,endTime,ip);
    }

    @Override
    public PageModel<UserPaymentDetailModel> page(Collection<String> ids, UserPaymentStatus status, UserPaymentType type, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<UserPaymentDetailModel> data = search(ids,status,type,userId,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(ids,status,type,userId,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }

    @Override
    public UserPaymentDetailModel getDetail(String id) {
        List<UserPaymentDetailModel> list = search(new HashSet<String>(){{ add(id);}},null,null,null,null,null,null,null,null,null);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }
}
