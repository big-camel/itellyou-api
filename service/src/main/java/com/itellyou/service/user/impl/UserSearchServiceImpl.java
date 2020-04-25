package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserInfoDao;
import com.itellyou.model.sys.PageModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.user.UserNotificationDetailModel;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Service
public class UserSearchServiceImpl implements UserSearchService {
    private final UserInfoDao infoDao;

    @Autowired
    public UserSearchServiceImpl(UserInfoDao infoDao){
        this.infoDao = infoDao;
    }

    @Override
    public UserInfoModel findByToken(String token , Long time) {
        return infoDao.findByToken(token,time);
    }

    /**
     * 根据在过去（86400 * 360）秒的有效期内的token获取用户信息
     * @param token
     * @return
     */
    public UserInfoModel findByToken(String token) {
        Long time = DateUtils.getTimestamp() - 86400 * 360;
        return findByToken(token,time);
    }

    @Override
    public UserInfoModel findByName(String name) {
        return infoDao.findByName(name);
    }

    @Override
    public UserInfoModel findByLoginName(String loginName) {
        return infoDao.findByLoginName(loginName);
    }

    @Override
    public UserInfoModel findByMobile(String mobile) {
        return findByMobile(mobile,null);
    }

    @Override
    public UserInfoModel findByMobile(String mobile,Integer mobileStatus) {
        return infoDao.findByMobile(mobile,mobileStatus);
    }

    @Override
    public UserInfoModel findByEmail(String email,Integer emailStatus) {
        return infoDao.findByEmail(email,emailStatus);
    }

    @Override
    public UserInfoModel findByEmail(String email) {
        return findByEmail(email,null);
    }

    @Override
    public UserInfoModel findById(Long id) {
        return infoDao.findById(id);
    }

    @Override
    public List<UserDetailModel> search(HashSet<Long> ids,
                                        Long searchUserId,
                                        String loginName, String name,
                                        String mobile, String email,
                                        Long beginTime, Long endTime,
                                        Long ip,
                                        Map<String, String> order,
                                        Integer offset,
                                        Integer limit){
        return infoDao.search(ids,searchUserId,loginName,name,mobile,email,beginTime,endTime,ip,order,offset,limit);
    }

    @Override
    public int count(HashSet<Long> ids, String loginName, String name, String mobile, String email, Long beginTime, Long endTime, Long ip) {
        return infoDao.count(ids,loginName,name,mobile,email,beginTime,endTime,ip);
    }

    @Override
    public PageModel<UserDetailModel> page(HashSet<Long> ids, Long searchUserId, String loginName, String name, String mobile, String email, Long beginTime, Long endTime, Long ip, Map<String, String> order, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        List<UserDetailModel> data = search(ids,searchUserId,loginName,name,mobile,email,beginTime,endTime,ip,order,offset,limit);
        Integer total = count(ids,loginName,name,mobile,email,beginTime,endTime,ip);
        return new PageModel<>(offset,limit,total,data);
    }

    @Override
    public UserDetailModel find(Long id, Long searchId) {
        List<UserDetailModel> detailModels = search(new LinkedHashSet<Long>(){{add(id);}},searchId,null,null,null,null,null,null,null,null,0,1);
        if(detailModels != null && detailModels.size() > 0) return detailModels.get(0);
        return null;
    }
}
