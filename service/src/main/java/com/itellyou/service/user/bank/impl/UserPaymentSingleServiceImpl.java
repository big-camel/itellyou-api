package com.itellyou.service.user.bank.impl;

import com.itellyou.dao.user.UserPaymentDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.user.UserPaymentModel;
import com.itellyou.model.user.UserPaymentStatus;
import com.itellyou.model.user.UserPaymentType;
import com.itellyou.service.user.bank.UserPaymentSingleService;
import com.itellyou.util.RedisUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@CacheConfig(cacheNames = CacheKeys.PAYMENT_KEY)
@Service
public class UserPaymentSingleServiceImpl implements UserPaymentSingleService {

    private final UserPaymentDao paymentDao;

    public UserPaymentSingleServiceImpl(UserPaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    @Override
    public UserPaymentModel find(String id) {
        List<UserPaymentModel> list = search(new HashSet<String>(){{add(id);}},null,null,null,null,null,null,null,null,null);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<UserPaymentModel> search(Collection<String> ids, UserPaymentStatus status, UserPaymentType type, Long userId, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return RedisUtils.fetch(CacheKeys.PAYMENT_KEY, UserPaymentModel.class,ids,(Collection<String> fetchIds) ->
                paymentDao.search(fetchIds,status,type,userId,beginTime,endTime,ip,order,offset,limit)
        );
    }
}
