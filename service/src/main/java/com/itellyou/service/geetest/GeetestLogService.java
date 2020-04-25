package com.itellyou.service.geetest;

import com.itellyou.model.geetest.GeetestClientTypeEnum;
import com.itellyou.model.geetest.GeetestLogModel;
import com.itellyou.model.geetest.GeetestModel;

public interface GeetestLogService {

    int insert(GeetestLogModel geetestLogModel);

    GeetestLogModel findByKey(String key);
}
