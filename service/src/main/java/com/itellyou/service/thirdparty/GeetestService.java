package com.itellyou.service.thirdparty;

import com.itellyou.model.thirdparty.GeetestClientTypeEnum;
import com.itellyou.model.thirdparty.GeetestModel;
import com.itellyou.model.thirdparty.GeetestResultModel;

public interface GeetestService {

    boolean verify(GeetestResultModel geetestResultModel);

    GeetestModel init(String mode, Long userId, GeetestClientTypeEnum clientType, String ip);
}
