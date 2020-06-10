package com.itellyou.service.user.passport;

import javax.servlet.http.HttpServletResponse;

public interface UserLoginService {

    String createToken(Long userId,Long ip);

    void sendToken(HttpServletResponse response, String token);
}
