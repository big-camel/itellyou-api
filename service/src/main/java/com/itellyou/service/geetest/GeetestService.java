package com.itellyou.service.geetest;

import com.itellyou.model.geetest.GeetestClientTypeEnum;
import com.itellyou.model.geetest.GeetestModel;
import com.itellyou.model.geetest.GeetestResultModel;

public interface GeetestService {

    boolean verify(GeetestResultModel geetestResultModel);

    GeetestModel init(String mode, Long userId, GeetestClientTypeEnum clientType, String ip);
}
