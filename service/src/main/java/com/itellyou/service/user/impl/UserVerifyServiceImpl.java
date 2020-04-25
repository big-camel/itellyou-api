package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserVerifyDao;
import com.itellyou.model.user.UserVerifyModel;
import com.itellyou.service.user.UserVerifyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserVerifyServiceImpl implements UserVerifyService {

    private final UserVerifyDao verifyDao;

    public UserVerifyServiceImpl(UserVerifyDao verifyDao){
        this.verifyDao = verifyDao;
    }

    @Override
    public int insert(UserVerifyModel model) {
        return verifyDao.insert(model);
    }

    @Override
    public List<UserVerifyModel> search(String key, String userId, Boolean isDisabled, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        return verifyDao.search(key,userId,isDisabled,beginTime,endTime,ip,order,offset,limit);
    }
}
