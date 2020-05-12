package com.itellyou.service.thirdparty;

import com.itellyou.model.thirdparty.GeetestLogModel;

public interface GeetestLogService {

    int insert(GeetestLogModel geetestLogModel);

    GeetestLogModel findByKey(String key);
}
