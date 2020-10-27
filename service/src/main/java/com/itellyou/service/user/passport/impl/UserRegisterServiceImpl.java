package com.itellyou.service.user.passport.impl;

import com.itellyou.dao.user.UserInfoDao;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.sys.SysPath;
import com.itellyou.model.sys.SysPathModel;
import com.itellyou.model.user.UserBankModel;
import com.itellyou.model.user.UserInfoModel;
import com.itellyou.service.common.impl.IndexFactory;
import com.itellyou.service.common.IndexService;
import com.itellyou.service.sys.SysPathService;
import com.itellyou.service.user.bank.UserBankService;
import com.itellyou.service.user.passport.UserRegisterService;
import com.itellyou.util.DateUtils;
import com.itellyou.util.IPUtils;
import com.itellyou.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class UserRegisterServiceImpl implements UserRegisterService {

    private final UserInfoDao infoDao;
    private final UserBankService bankService;
    private final IndexService<UserInfoModel> indexService;
    private final SysPathService pathService;

    @Autowired
    public UserRegisterServiceImpl(UserInfoDao infoDao,UserBankService bankService,SysPathService pathService,IndexFactory indexFactory){
        this.infoDao = infoDao;
        this.bankService = bankService;
        this.indexService = indexFactory.create(EntityType.USER);
        this.pathService = pathService;
    }

    @Override
    @Transactional
    public Long mobile(String name, String password, String mobile,String ip) {
        try {
            password = StringUtils.isEmpty(password) ? null : StringUtils.encoderPassword(password);
            UserInfoModel userInfoModel = new UserInfoModel();
            userInfoModel.setName(name);
            userInfoModel.setAvatar("https://cdn-object.itellyou.com/avatar/default.png");
            userInfoModel.setLoginPassword(password);
            userInfoModel.setMobile(mobile);
            userInfoModel.setMobileStatus(true);
            userInfoModel.setDisabled(false);
            userInfoModel.setCreatedTime(DateUtils.toLocalDateTime());
            userInfoModel.setCreatedIp(ip != null ? IPUtils.toLong(ip) : 0);
            int result = infoDao.createUser(userInfoModel);
            if(result != 1){
                throw new Exception("创建用户失败");
            }
            String path = "u_" + userInfoModel.getId();
            SysPathModel pathModel = pathService.findByPath(path);
            if(pathModel != null){
                path += "_" + DateUtils.getTimestamp();
            }
            result = pathService.insert(new SysPathModel(path, SysPath.USER,userInfoModel.getId()));
            if(result != 1) throw new Exception("创建用户路径失败");
            UserBankModel bankModel = new UserBankModel(userInfoModel.getId(),0,0.0,0);
            result = bankService.insert(bankModel);
            if(result != 1){
                throw new Exception("创建用户余额失败");
            }

            indexService.createIndex(userInfoModel.getId());
            return userInfoModel.getId();
        }catch (Exception e){
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return null;
    }
}
