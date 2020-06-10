package com.itellyou.service.user;

import com.itellyou.model.user.UserInfoModel;

public interface UserSingleService {

    UserInfoModel findByName(String name);

    UserInfoModel findByLoginName(String loginName);

    UserInfoModel findByMobile(String mobile,Integer mobileStatus);

    UserInfoModel findByMobile(String mobile);

    UserInfoModel findByEmail(String email,Integer emailStatus);

    UserInfoModel findByEmail(String email);

    UserInfoModel findById(Long id);

}
