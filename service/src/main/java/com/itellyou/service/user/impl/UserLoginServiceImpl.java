package com.itellyou.service.user.impl;

import com.itellyou.model.user.UserLoginLogModel;
import com.itellyou.service.user.UserLoginLogService;
import com.itellyou.service.user.UserLoginService;
import com.itellyou.util.CookieUtils;
import com.itellyou.util.DateUtils;
import com.itellyou.util.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    private final UserLoginLogService logService;

    public UserLoginServiceImpl(UserLoginLogService logService) {
        this.logService = logService;
    }

    @Override
    public String createToken(Long userId, Long ip) {
        String token = StringUtils.createToken(userId.toString());

        UserLoginLogModel logModel = new UserLoginLogModel(token,false,"web", DateUtils.getTimestamp(),userId, ip);
        int result = logService.insert(logModel);
        if(result != 1){
            return null;
        }
        return token;
    }

    @Override
    public void sendToken(HttpServletResponse response, String token) {
        CookieUtils.setCookie(response,"token",token,"/",86400 * 360);
    }
}
