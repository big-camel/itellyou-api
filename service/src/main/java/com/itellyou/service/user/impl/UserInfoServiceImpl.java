package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserInfoDao;
import com.itellyou.model.constant.CacheKeys;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.event.UserEvent;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.event.OperationalPublisher;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = CacheKeys.USER_INFO_KEY)
@Service
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoDao userInfoDao;

    private final OperationalPublisher operationalPublisher;

    @Autowired
    public UserInfoServiceImpl(UserInfoDao userInfoDao, OperationalPublisher operationalPublisher){
        this.userInfoDao = userInfoDao;
        this.operationalPublisher = operationalPublisher;
    }

    @Override
    @CacheEvict
    public int updateByUserId(UserInfoModel infoModel) {
        if(StringUtils.isNotEmpty(infoModel.getLoginPassword())){
            infoModel.setLoginPassword(StringUtils.encoderPassword(infoModel.getLoginPassword()));
        }
        if(StringUtils.isNotEmpty(infoModel.getPayPassword())){
            infoModel.setPayPassword(StringUtils.encoderPassword(infoModel.getPayPassword()));
        }
        int result = userInfoDao.updateByUserId(infoModel);
        if(result == 1 && (StringUtils.isNotEmpty(infoModel.getName()) || StringUtils.isNotEmpty(infoModel.getAvatar()))){
            operationalPublisher.publish(new UserEvent(this, EntityAction.UPDATE,infoModel.getId(),infoModel.getCreatedUserId(),infoModel.getUpdatedUserId(), DateUtils.toLocalDateTime(),infoModel.getUpdatedIp()));
        }
        return result;
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateStarCount(Long id, Integer step) {
        return userInfoDao.updateStarCount(id,step);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateFollowerCount(Long id, Integer step) {
        return userInfoDao.updateFollowerCount(id,step);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateQuestionCount(Long id, Integer step) {
        return userInfoDao.updateQuestionCount(id,step);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateAnswerCount(Long id, Integer step) {
        return userInfoDao.updateAnswerCount(id,step);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateArticleCount(Long id, Integer step) {
        return userInfoDao.updateArticleCount(id,step);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateColumnCount(Long id, Integer step) {
        return userInfoDao.updateColumnCount(id,step);
    }

    @Override
    @CacheEvict(key = "#id")
    public int updateCollectionCount(Long id, Integer step) {
        return userInfoDao.updateCollectionCount(id,step);
    }
}
