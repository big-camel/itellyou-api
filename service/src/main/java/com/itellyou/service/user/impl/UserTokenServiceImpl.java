package com.itellyou.service.user.impl;

import com.itellyou.model.user.UserInfoModel;
import com.itellyou.model.user.UserLoginLogModel;
import com.itellyou.service.user.UserLoginLogService;
import com.itellyou.service.user.UserSearchService;
import com.itellyou.service.user.UserTokenService;
import com.itellyou.util.DateUtils;
import org.springframework.stereotype.Service;

@Service
public class UserTokenServiceImpl implements UserTokenService {

    private final UserSearchService searchService;
    private final UserLoginLogService logService;

    public UserTokenServiceImpl(UserSearchService searchService, UserLoginLogService logService) {
        this.searchService = searchService;
        this.logService = logService;
    }

    @Override
    public UserInfoModel find(String token, Long time) {
        UserLoginLogModel loginLogModel = logService.find(token);
        if(loginLogModel == null || loginLogModel.isDisabled() || loginLogModel.getCreatedTime() < time) return null;
        return searchService.findById(loginLogModel.getCreatedUserId());
    }

    @Override
    public UserInfoModel find(String token) {
        Long time = DateUtils.getTimestamp() - 86400 * 360;
        return find(token,time);
    }
}
