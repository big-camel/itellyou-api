package com.itellyou.service.user.impl;

import com.itellyou.dao.user.UserInfoDao;
import com.itellyou.model.sys.SysPath;
import com.itellyou.model.sys.SysPathModel;
import com.itellyou.model.user.UserBankModel;
import com.itellyou.model.user.UserDetailModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.sys.SysPathService;
import com.itellyou.service.user.UserBankService;
import com.itellyou.service.user.UserIndexService;
import com.itellyou.service.user.UserInfoService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoDao userInfoDao;
    private final UserBankService bankService;
    private final UserIndexService indexService;
    private final SysPathService pathService;

    @Autowired
    public UserInfoServiceImpl(UserInfoDao userInfoDao,UserBankService bankService,UserIndexService indexService,SysPathService pathService){
        this.userInfoDao = userInfoDao;
        this.bankService = bankService;
        this.indexService = indexService;
        this.pathService = pathService;
    }

    @Override
    public int updateByUserId(UserInfoModel infoModel) {
        if(StringUtils.isNotEmpty(infoModel.getLoginPassword())){
            infoModel.setLoginPassword(StringUtils.encoderPassword(infoModel.getLoginPassword()));
        }
        if(StringUtils.isNotEmpty(infoModel.getPayPassword())){
            infoModel.setPayPassword(StringUtils.encoderPassword(infoModel.getPayPassword()));
        }
        int result = userInfoDao.updateByUserId(infoModel);
        if(result == 1 && (StringUtils.isNotEmpty(infoModel.getName()) || StringUtils.isNotEmpty(infoModel.getAvatar()))){
            indexService.update(infoModel);
        }
        return result;
    }

    @Override
    public int updateStarCount(Long id, Integer step) {
        return userInfoDao.updateStarCount(id,step);
    }

    @Override
    public int updateFollowerCount(Long id, Integer step) {
        return userInfoDao.updateFollowerCount(id,step);
    }

    @Override
    public int updateQuestionCount(Long id, Integer step) {
        return userInfoDao.updateQuestionCount(id,step);
    }

    @Override
    public int updateAnswerCount(Long id, Integer step) {
        return userInfoDao.updateAnswerCount(id,step);
    }

    @Override
    public int updateArticleCount(Long id, Integer step) {
        return userInfoDao.updateArticleCount(id,step);
    }

    @Override
    public int updateColumnCount(Long id, Integer step) {
        return userInfoDao.updateColumnCount(id,step);
    }

    @Override
    public int updateCollectionCount(Long id, Integer step) {
        return userInfoDao.updateCollectionCount(id,step);
    }
}
